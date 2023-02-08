package com.itheima.sh.stock.service.impl;

import com.google.common.collect.Lists;
import com.itheima.sh.stock.config.StockInfoConfig;
import com.itheima.sh.stock.mapper.StockBusinessMapper;
import com.itheima.sh.stock.mapper.StockMarketIndexInfoMapper;
import com.itheima.sh.stock.mapper.StockRtInfoMapper;
import com.itheima.sh.stock.pojo.domain.InnerMarketDomain;
import com.itheima.sh.stock.pojo.entity.StockMarketIndexInfo;
import com.itheima.sh.stock.pojo.entity.StockRtInfo;
import com.itheima.sh.stock.service.StockTimerTaskService;
import com.itheima.sh.stock.utils.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class StockTimerTaskServiceImpl implements StockTimerTaskService {
    //装配StockInfoConfig配置类对象
    @Autowired
    private StockInfoConfig stockInfoConfig;

    //装配RestTemplate类对象
    @Autowired
    private RestTemplate restTemplate;

    //装配IdWorker类对象
    @Autowired
    private IdWorker getIdWorker;

    //装配StockMarketIndexInfo类对象
    @Autowired
    private StockMarketIndexInfoMapper stockMarketIndexInfoMapper;

    //装配StockBusinessMapper对象
    @Autowired
    private StockBusinessMapper stockBusinessMapper;

    //装配StockBusinessMapper对象
    @Autowired
    private ParserStockInfoUtil parserStockInfoUtil;


    //装配StockRtInfoMapper对象
    @Autowired
    private StockRtInfoMapper stockRtInfoMapper;

    //装配RabbitTemplate对象  发送消息到mq
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 获取国内大盘的实时数据信息
     */
    @Override
    public void getInnerMarketInfo() {
        //1.使用配置类StockInfoConfig获取大盘的地址和大盘编码，然后拼接成大盘完整的地址
        //1.1获取新浪服务器提供的大盘地址前面部分
        String marketUrl = stockInfoConfig.getMarketUrl();//"http://hq.sinajs.cn/list="
        //1.2获取新浪服务器提供的大盘地址后面面部分即大盘编码
        List<String> innerList = stockInfoConfig.getInner();//["sh000001","sz399001"]
        //1.3拼接上述地址
        //String.join(",",innerList); 表示将innerList集合中的数据中间使用逗号拼接"sh000001,sz399001"一个完整字符串
        //"http://hq.sinajs.cn/list="+"sh000001,sz399001"==》"http://hq.sinajs.cn/list=sh000001,sz399001"
        String url = marketUrl + String.join(",", innerList);
        //2.创建HttpHeaders对象
        HttpHeaders headers = new HttpHeaders();
        //3.使用HttpHeaders对象对象调用add方法添加Referer和User-Agent
        headers.add("Referer", "https://finance.sina.com.cn");
        headers.add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.110 Safari/537.36");
        //4.根据上述的请求头组装请求HttpEntity对象
        HttpEntity httpEntity = new HttpEntity(headers);
        //5.使用resetTemplate调用exchange方法向新浪服务器发送请求并使用ResponseEntity对象接收
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        //6.取出响应体数据
        String responseBodyStr = responseEntity.getBody();
        //7.定义解析的正则的字符串对象
        /*
            响应的字符串：
                var hq_str_sh000001="上证指数,3275.6578,3285.6705,3263.4059,3275.6578,3235.3533,0,0,293342814,378648522965,
                0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2023-02-03,15:30:39,00,";
                1）var hq_str_ ===》 匹配上述字符串中前面的内容var hq_str_
                2）(.+)   ===》   匹配上述字符串中的 sh000001
                3）="  ===》   匹配上述字符串中的 ="
                4）(.)+===》匹配上述字符串中从 上证指数开始一直到....39,00,
                5)";  ===》匹配上述字符串中";
         */
        String pattern = "var hq_str_(.+)=\"(.+)\";";
        //8.编译正则表达式,获取编译Pattern对象
        Pattern r = Pattern.compile(pattern);
        //9.根据编译对象调用matcher方法获取匹配器Matcher对象匹配字符串即响应体数据
        Matcher m = r.matcher(responseBodyStr);
        //10.创建List集合泛型是StockMarketIndexInfo来存储国内大盘信息
        ArrayList<StockMarketIndexInfo> marketIndexInfoList = new ArrayList<>();
        //11.判断是否有匹配的数值
        while (m.find()) {
            //12.获取大盘的code
            //参数1表示上述正则中第一组即(.)
            String marketCode = m.group(1);//"sh000001" "sz399001"
            //13.获取其它信息，字符串以逗号间隔
            /*
                otherInfo就是===>上证指数,3275.6578,3285.6705,3263.4059,3275.6578,3235.3533,0,0,293342814,378648522965,
                            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2023-02-03,15:30:39,00,
             */
            String otherInfo = m.group(2);//2表示上数字正则的第二组
            //14.以逗号切割上述获取的其它信息字符串，形成数组
            String[] splitArr = otherInfo.split(",");
            //15.分别获取大盘名称 开盘点数 前收盘点 当前点数 最高点 最低点 成交量 成交金额 时间
            //15.1获取大盘名称
            String marketName = splitArr[0];//"上证指数"
            //15.2获取开盘点数
            BigDecimal openPoint = new BigDecimal(splitArr[1]);
            //15.3前收盘点 3283.4261
            BigDecimal preClosePoint = new BigDecimal(splitArr[2]);
            //15.4获取大盘的当前点数 3236.6951
            BigDecimal curPoint = new BigDecimal(splitArr[3]);
            //15.5获取大盘最高点 3290.2561
            BigDecimal maxPoint = new BigDecimal(splitArr[4]);
            //15.6 获取大盘的最低点 3236.4791
            BigDecimal minPoint = new BigDecimal(splitArr[5]);
            //15.7获取成交量 402626660
            Long tradeAmt = Long.valueOf(splitArr[8]);
            //15.8获取成交金额 398081845473
            BigDecimal tradeVol = new BigDecimal(splitArr[9]);
            //15.9时间 2022-04-07 15:01:09
            Date curTime = DateTimeUtil.getDateTimeWithoutSecond(splitArr[30] + " " + splitArr[31]).toDate();

            //16.将解析的数据组装到StockMarketIndexInfo对象中
            StockMarketIndexInfo info = StockMarketIndexInfo.builder()
                    .id(getIdWorker.nextId())
                    .marketCode(marketCode)
                    .marketName(marketName)
                    .curPoint(curPoint)
                    .openPoint(openPoint)
                    .preClosePoint(preClosePoint)
                    .maxPoint(maxPoint)
                    .minPoint(minPoint)
                    .tradeVolume(tradeVol)
                    .tradeAmount(tradeAmt)
                    .curTime(curTime)
                    .build();
            //17.收集封装的对象到list集合中，方便批量插入
            marketIndexInfoList.add(info);
        }
        System.out.println(marketIndexInfoList);
        //18.判断封装的集合是否为空，如果为空不能向下执行即不能批量插入
        if(CollectionUtils.isEmpty(marketIndexInfoList)){
            return;//这里是跟新浪交互
        }
        //19.如果不为空在批量插入
        stockMarketIndexInfoMapper.insertBach(marketIndexInfoList);

        ////////////////////////////////////////////////////////////////////////////////////////////
        //上述是从新浪服务器采集的大盘数据插入到mysql数据库中了，接下来我们需要将，采集的数据同步到redis中一份
        //这里先将数据发送到mq中
        //1.将从数据库查询的集合中的StockMarketIndexInfo转换为InnerMarketDomain类型
        List<InnerMarketDomain> mqAndRedisInnerMarketDomainList = marketIndexInfoList.stream()
                .map(marketIndexInfo -> DomainTransferUtil.transferMarketInfo(marketIndexInfo))
                .collect(Collectors.toList());
        //2.使用rabbitTemplate调用convertAndSend方法向mq中发送消息，并指定交换机 routingkey
        rabbitTemplate.convertAndSend("stockExchange","inner.market",mqAndRedisInnerMarketDomainList);

    }
    /**
     * 定义获取分钟级个股股票数据
     */
    @Override
    public void getStockRtIndex() {
        //1.到数据表stock_business中获取股票编码集合===》000001，000002，000003,.....
        List<String> stockCodes = stockBusinessMapper.getStockCodes();//{000001，000002，000003,.....}
        //2.计算出符合新浪命名规范的股票编码数据
        //stockCodes={"sz000001","sz000002",...}
        stockCodes = stockCodes.stream().map(code -> code.startsWith("6") ? ("sh" + code) : ("sz" + code)).collect(Collectors.toList());
        //3.设置公共请求头HttpHeaders对象
        HttpHeaders headers = new HttpHeaders();
        //4.创建请求头数据,目的是可以访问新浪服务器  Referer User-Agent
        headers.add("Referer","https://finance.sina.com.cn");
        headers.add("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.110 Safari/537.36");
        //5.将请求头对象放到请求实体HttpEntity对象中
        HttpEntity httpEntity = new HttpEntity(headers);
        //6.一次性查询过多，我们将需要查询的数据先进行分片处理，每次最多查询20条股票数据，因此可以使用Stream的forEach方法处理
//        List<List<String>> partitionList = Lists.partition(stockCodes, 20);
        Lists.partition(stockCodes, 20).stream().forEach(partitionListCode->{
            /*
                partitionListCode={"sz000001","sz000002",...} 一次是20个，我们需要将该集合中的每个元素取出拼接到
                向新浪服务器发送请求的url后面：
                http://hq.sinajs.cn/list=sz000001,sz000002
             */
            //7.使用stockInfoConfig对象获取大盘地址并拼接股票编码
            //stockInfoConfig.getMarketUrl() ===>http://hq.sinajs.cn/list=
            //结果： http://hq.sinajs.cn/list=sz000001,sz000002
            //下面的url就是向新浪服务器发送请求的地址
            String url = stockInfoConfig.getMarketUrl()+String.join(",",partitionListCode);
            //8.使用restTemplate.postForObject()方法获取响应数据
//            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
            /*
                新浪服务器响应的关于个股的数据：
                var hq_str_sh601003="柳钢股份,3.770,3.770,3.770,3.790,3.760,3.770,3.780,509100,1923203.00...
             */
//            String body = responseEntity.getBody();
            //下面的代码相当于上述两行代码
            String innerStockRtInfo = restTemplate.postForObject(url, httpEntity, String.class);
            //9.使用工具类对象parserStockInfoUtil调用方法将A股个股数据封装到实体类StockRtInfo中并存放到List集合中
            //innerStockRtInfo :表示新浪服务器响应的个股数据，ParseType.ASHARE的值是3表示个股标识
            List<StockRtInfo> list = parserStockInfoUtil.parser4StockOrMarketInfo(innerStockRtInfo, ParseType.ASHARE);
            //输出
            //System.out.println("list = " + list);
            //将上述list集合的数据批量插入到stock_rt_info个股表中
            stockRtInfoMapper.insertBach(list);
        });



    }
}
