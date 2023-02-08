package com.itheima.sh.stock.service.impl;

import com.alibaba.excel.EasyExcel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itheima.sh.stock.config.StockInfoConfig;
import com.itheima.sh.stock.mapper.StockMarketIndexInfoMapper;
import com.itheima.sh.stock.mapper.StockRtInfoMapper;
import com.itheima.sh.stock.pojo.domain.InnerMarketDomain;
import com.itheima.sh.stock.pojo.domain.Stock4EvrDayDomain;
import com.itheima.sh.stock.pojo.domain.Stock4MinuteDomain;
import com.itheima.sh.stock.pojo.domain.StockUpdownDomain;
import com.itheima.sh.stock.pojo.vo.PageResult;
import com.itheima.sh.stock.pojo.vo.R;
import com.itheima.sh.stock.pojo.vo.ResponseCode;
import com.itheima.sh.stock.service.StockService;
import com.itheima.sh.stock.utils.DateTimeUtil;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.*;

@Service("stockServiceImpl")
public class StockServiceImpl implements StockService {
    //1.自动装配国内大盘数据StockMarketIndexInfoMapper对象
    @Autowired
    private StockMarketIndexInfoMapper stockMarketIndexInfoMapper;
    //2.自动装配大盘ID集合的配置类StockInfoConfig对象
    @Autowired
    private StockInfoConfig stockInfoConfig;

    //3.自动装配国内个股数据StockRtInfoMapper对象
    @Autowired
    private StockRtInfoMapper stockRtInfoMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /*
        3.定义方法查询国内大盘数据
     */
    @Override
    public R<List<InnerMarketDomain>> innerIndexAll() {
        //从redis中获取大盘数据
        /*
            range("innerMarketInfos", 0, -1); 表示从redis中获取数据，
                1)第一个参数表示redis中的键，
                2)第二个参数表示list类型的起始索引 0
                3)第三个参数表示list类型的结束索引 -1
         */
        List<InnerMarketDomain> innerMarketInfos = redisTemplate.opsForList().range("innerMarketInfos", 0, -1);
        //判断从redis取出的数据集合是否为空
        if(!CollectionUtils.isEmpty(innerMarketInfos)){
            //说明redis中有数据，直接将数据响应给web
            return R.ok(innerMarketInfos);
        }
        //程序执行到这里说明redis中没有数据，执行下面的代码到mysql中查询数据

        //4.使用工具类DateTimeUtil获取最新的股票交易时间点
        DateTime curDateTime = DateTimeUtil.getLastDate4Stock(DateTime.now());
        Date curDate = curDateTime.toDate();
        //TODO:5.注意：目前数据表中没有最新股票的数据，后期我们学习定时任务更新到数据表中就有了，
        // 那么这里先伪造数据，后续删除
        curDate = DateTime.parse("2021-12-28 09:31:00",
                DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();

        //6.使用配置类对象调用方法获取国内大盘编码集合
        //innerList=['sh000001', 'sz399001']
        List<String> innerList = stockInfoConfig.getInner();
        //7.使用StockMarketIndexInfoMapper对象调用方法获取大盘数据，将最新时间和大盘集合作为参数
        List<InnerMarketDomain> innerMarketDataList = stockMarketIndexInfoMapper.innerIndexAll(curDate, innerList);

        ////////////////////////////////////////////////////////////////////////////////////////////////
        //将从mysql中查询的数据同步到redis中
        redisTemplate.opsForList().leftPushAll("innerMarketInfos", innerMarketDataList);
        //8.将查询的结果返回给web层
        return R.ok(innerMarketDataList);
    }

    /*
       定义方法查询国内个股数据并按照涨幅降序排序
    */
    @Override
    public R<PageResult> getStockPageInfo(Integer page, Integer pageSize) {
        //1.设置PageHelper分页参数,将当前页码和每页条数使用PageHelper的startPage方法传递到底层
        PageHelper.startPage(page, pageSize);
        //2.获取当前最新的股票交易时间点，需要使用假数据"2022-06-07 15:00:00"
        DateTime curDateTime = DateTimeUtil.getLastDate4Stock(DateTime.now());
        Date curDate = curDateTime.toDate();
        //TODO:后期需要删除下面的代码，定时任务开启就直接使用当前系统时间即上述的时间
        curDate = DateTime.parse("2022-06-07 15:00:00",
                DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //3.调用mapper接口方法分页查询股票最新数据，并按照涨幅排序查询
        List<StockUpdownDomain> stockUpdownList = stockRtInfoMapper.getStockPageInfo(curDate);
        //4.判断查询的集合数据是否为空，如果查询的数据集合是空，则响应给前端错误信息
        if (CollectionUtils.isEmpty(stockUpdownList)) {
            //说明集合是空的，则响应给前端错误信息
            return R.error(ResponseCode.NO_RESPONSE_DATA);
        }
        //5.创建PageHelper的PageInfo的对象，将查询的结果集合作为构造方法参数传递，底层就会分页查询数据，
        // 并数据封装到PageInfo对象中
        PageInfo pageInfo = new PageInfo(stockUpdownList);
        //6.创建PageResult对象组装PageInfo对象，获取分页的具体信息,因为PageInfo包含了丰富的分页信息，
        // 而部分分页信息是前端不需要的
        PageResult pageResult = new PageResult(pageInfo);
        //7.将组装的数据响应给web层
        return R.ok(pageResult);
    }

    /*
       功能描述：统计沪深两市T日(当前股票交易日)每分钟达到涨跌停股票的数据
     */
    @Override
    public R<Map<String, List<Map<String, Object>>>> getStockUpdownCount() {
        //1.使用DateTimeUtil获取最新股票交易时间点，暂时使用假数据2022-01-06 14:25:00赋值给最新交易时间的变量
        DateTime curDateTime = DateTimeUtil.getLastDate4Stock(DateTime.now());
        Date curDate = curDateTime.toDate();
        //TODO:后期删除
        curDate = DateTime.parse("2022-01-06 14:25:00",
                DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //2.使用DateTimeUtil调用方法获取最新交易时间对应的开盘时间，暂时使用假数据2022-01-06 09:30:00
        // public static DateTime getOpenDate(DateTime dateTime){}
        DateTime openDateTime = DateTimeUtil.getOpenDate(curDateTime);
        Date openDate = openDateTime.toDate();
        //TODO:后期删除
        openDate = DateTime.parse("2022-01-06 09:30:00",
                DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();

        //3.查询涨停数据并约定mapper中flag入参： 1：涨停数据 0：跌停
        List<Map<String, Object>> upList = stockRtInfoMapper.getStockUpdownCount(openDate, curDate, 1);
        //4.查询跌停数据
        List<Map<String, Object>> downList = stockRtInfoMapper.getStockUpdownCount(openDate, curDate, 0);
        //5.创建HashMap集合对象，组装上述查询的涨停和跌停的集合数据
        Map<String, List<Map<String, Object>>> map = new HashMap<>();
        map.put("upList", upList);
        map.put("downList", downList);
        //6.将HashMap集合对象返回给web层
        return R.ok(map);
    }

    /*
         涨幅信息数据导出功能实现
     */
    @Override
    public void stockExport(HttpServletResponse response, Integer page, Integer pageSize) {
        try {
            //1.获取最近最新的一次股票有效交易时间点（精确分钟）,临时指定一个假的日期数据  2022-06-07 15:00:00
            Date curDate = DateTimeUtil.getLastDate4Stock(DateTime.now()).toDate();
            //TODO:后期删除
            curDate = DateTime.parse("2022-06-07 15:00:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
            //2.设置分页参数 ,将接收的当前页码和每页显示数据条数通过PageHelper的startPage
            // 方法进行参数设置底层会拦截mybatis发送的sql，并动态追加limit语句实现分页
            PageHelper.startPage(page, pageSize);
            //3.使用StockRtInfoMapper对象调用分页查询最新股票数据，并按照涨幅排序的方法
            List<StockUpdownDomain> list = stockRtInfoMapper.getStockPageInfo(curDate);
            //4.对返回的集合判断是否为空，如果集合为空，响应错误提示信息。由于导出数据到表格的方法没有返回值，因此：
            if (CollectionUtils.isEmpty(list)) {
                //说明查询数据集合为空
                //4.1使用jackson的ObjectMapper类中的writeValueAsString方法将响应错误信息转化成json格式字符串
                /*
                    r:
                        code :0
                        msg:无响应数据
                 */
                R<Object> r = R.error(ResponseCode.NO_RESPONSE_DATA);
                /*
                    new ObjectMapper().writeValueAsString(r) 使用jackson包下的工具类将对象转换为json字符串可以使用fastJson
                 */
                String respJsonStr = new ObjectMapper().writeValueAsString(r);
                //4.2设置响应的数据格式 告知浏览器传入的数据格式
                /*
                    response.setContentType("application/json;charset=utf-8"); 设置响应头，告知浏览器服务器响应给浏览器
                    的数据格式：application/json，charset=utf-8告知浏览器使用的编码表是utf-8
                 */
                response.setContentType("application/json;charset=utf-8");
                //4.3响应数据
                response.getWriter().print(respJsonStr);
                //4.4 停止当前方法
                return;
            }

            //5.设置响应excel文件格式类型 告知浏览器响应数据是表格
            response.setContentType("application/vnd.ms-excel");
            //6.设置响应数据的编码格式
            response.setCharacterEncoding("utf-8");
            //7.设置默认的文件名称 下面的代码表示对象文件名进行编码
            String fileName = URLEncoder.encode("涨幅信息数据", "UTF-8");

            //8.设置默认文件名称：兼容一些特殊浏览器
            /*
                Content-disposition ：表示告知浏览器以附件形式下载
                attachment;filename= ： attachment表示附件，filename=后面跟着文件名
             */
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            //9.响应excel流
            //response.getOutputStream() 关联浏览器的字节输出流
            //StockUpdownDomain.class 将该类的属性值写出到表格中
            EasyExcel.write(response.getOutputStream(), StockUpdownDomain.class).sheet("个股涨幅信息统计")
                    .doWrite(list);//list表示从数据库查询的分页数据
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     成交量对比功能实现
   */
    @Override
    public R<Map<String, List<Map<String, Object>>>> stockTradeVol4InnerMarket() {
        //实现步骤：
        //1.获取最近股票有效交易时间点，T日当前时间
        DateTime curDateTime4T = DateTimeUtil.getLastDate4Stock(DateTime.now());
        //2.根据上述获取的当前时间获取T日股票开盘时间
        DateTime openDateTime4T = DateTimeUtil.getOpenDate(curDateTime4T);
        //3.分别将上述获取的DateTime类型的时间转换为Date,这样jdbc默认识别
        Date curDate4T = curDateTime4T.toDate();//T日当前时间
        Date openDate4T = openDateTime4T.toDate();//T日股票开盘时间

        //4.分别准备假数据覆盖上述当前时间(2022-01-03 14:40:00)和开盘时间(2022-01-03 09:30:00)
        //TODO:后期删除
        curDate4T = DateTime.parse("2022-01-03 14:40:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        openDate4T = DateTime.parse("2022-01-03 09:30:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();

        //5.使用DateTimeUtil调用getPreviousTradingDay方法根据上述获取T日的当前时间获取T-1日的当前时间
        //获取指定日期下股票的上一个有效交易日时间
        DateTime curDateTime4TminusOne = DateTimeUtil.getPreviousTradingDay(curDateTime4T);
        //6.使用DateTimeUtil调用方法根据T-1日的当前时间获取T-1日的股票有效交易日的开盘时间
        DateTime openDateTime4TminusOne = DateTimeUtil.getOpenDate(curDateTime4TminusOne);
        //7.分别将上述获取的DateTime类型的时间转换为Date
        Date curDate4TminusOne = curDateTime4TminusOne.toDate();
        Date openDate4TminusOne = openDateTime4TminusOne.toDate();
        //8.分别准备假数据覆盖上述当前时间(2022-01-02 14:40:00)和开盘时间(2022-01-02 09:30:00)
        //TODO:后期删除
        curDate4TminusOne = DateTime.parse("2022-01-02 14:40:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        openDate4TminusOne = DateTime.parse("2022-01-02 09:30:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //9.获取上证和深证的配置的大盘id
        List<String> innerMarketCodeList = stockInfoConfig.getInner();//"sh000001" "sz399001"

        //10.根据大盘id以及当前时间和开盘时间查询T日大盘交易统计数据
        List<Map<String, Object>> innerMarketAmt4T = stockMarketIndexInfoMapper.stockTradeVol4InnerMarket(innerMarketCodeList, curDate4T, openDate4T);
        //11.判断查询的T日大盘交易统计数据的集合是否为空，如果为空这里创建一个空的集合
        if (CollectionUtils.isEmpty(innerMarketAmt4T)) {
            //集合是空，主要考虑集合是null
            innerMarketAmt4T = new ArrayList<>();
        }
        //12.判断查询的T-1日大盘交易统计数据的集合是否为空，如果为空这里创建一个空的集合
        List<Map<String, Object>> innerMarketAmt4minusOne = stockMarketIndexInfoMapper.stockTradeVol4InnerMarket(innerMarketCodeList, curDate4TminusOne, openDate4TminusOne);
        if (CollectionUtils.isEmpty(innerMarketAmt4minusOne)) {
            //集合是空，主要考虑集合是null
            innerMarketAmt4minusOne = new ArrayList<>();
        }
        //13.组装响应数据，将查询的数据存放到Map集合中，注意Map集合的键是：amtList yesAmtList
        Map<String, List<Map<String, Object>>> map = new HashMap<>();
        map.put("amtList", innerMarketAmt4T);
        map.put("yesAmtList", innerMarketAmt4minusOne);
        //14.返回数据给web层
        return R.ok(map);
    }
    /*
          Stream流水线遍历集合方式优化后的个股涨跌幅度区间统计功能实现
   */
 /*   @Override
    public R<Map<String, Object>> getStockUpDown() {
        //1.获取股票最新一次交易的时间点，使用假的时间数据2022-01-06 09:55:00覆盖当前交易时间
        Date curDate = DateTimeUtil.getLastDate4Stock(DateTime.now()).toDate();
        //TODO:后期删除
        curDate = DateTime.parse("2022-01-06 09:55:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //2.根据当前时间查询股票信息
        List<Map> unOrderList = stockRtInfoMapper.getStockUpDown(curDate);

        //获取存储有序区间的集合
        List<String> orderList = stockInfoConfig.getUpDownRange();
        //创建list集合泛型是Map
        List<Map> orderListMaps = new ArrayList<>();
        //使用Stream流水线处理集合数据 orderList 集合的数据title就是区间 "<-7%" "-7~-5%" ....
        orderList.stream().map(title -> {
            //处理unOrderList集合 map={"count":15,"title":"-3~0%"}
            Optional<Map> optional = unOrderList.stream().filter(unOrderMap -> unOrderMap.containsValue(title)).findFirst();
            //创建Map集合对象
            Map map = new HashMap();
            //使用optional判断取出的数据是否存在
            if (optional.isPresent()) {
                //说明集合存在，获取数据
                map = optional.get();
            } else {
                //说明不存在
                map.put("count", 0);
                map.put("title", title);
            }
            //将map放到orderListMaps集合中
            orderListMaps.add(map);
            return orderListMaps;
        });

        //判断
        if(CollectionUtils.isEmpty(unOrderList)){
            //没有数据
            return R.error(ResponseCode.NO_RESPONSE_DATA);
        }
        //3.创建Map集合对象用来组装数据
        Map<String,Object> unOrderMap = new HashMap<String,Object>();
        unOrderMap.put("infos",orderListMaps);
//
        //4.使用DateTime构造方法将上述股票交易时间变为指定日期格式的字符串
        String timeStr = new DateTime(curDate).toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
        //5.将转换后的日期字符串和查询股票信息的集合存放到Map集合中
        unOrderMap.put("time",timeStr);//"time" "2022-01-06 09:55:00"
        //6.将数据返回给web层
        return R.ok(unOrderMap);
    }*/


    /* 传统遍历集合方式优化后的个股涨跌幅度区间统计功能实现*/

    @Override
    public R<Map<String, Object>> getStockUpDown() {
        //1.获取股票最新一次交易的时间点，使用假的时间数据2022-01-06 09:55:00覆盖当前交易时间
        Date curDate = DateTimeUtil.getLastDate4Stock(DateTime.now()).toDate();
        curDate = DateTime.parse("2022-01-06 09:55:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //2.根据当前时间查询股票信息
        List<Map> unOrderList = stockRtInfoMapper.getStockUpDown(curDate);

        //获取存储有序区间的集合
        List<String> orderList = stockInfoConfig.getUpDownRange();
        //创建list集合泛型是Map
        List<Map> orderListMaps = new ArrayList<>();
        //遍历orderList取出每个区间
        //title就是区间 "<-7%" "-7~-5%" ....
        for (String title : orderList) {
            //定义空的Map集合对象
            Map newMap = null;
            //遍历unOrderList集合取出Map集合
            //map={"count":15,"title":"-3~0%"}
            for (Map unOrderMap : unOrderList) {
                //判断取出的map的value是否包含title
                if (unOrderMap.containsValue(title)) {
                    //说明unOrderMap的值位置包含取出的title即区间
                    newMap = unOrderMap;//{"count":15,"title":"-3~0%"}
                    //将newMap放到
                    orderListMaps.add(newMap);
                    //结束当前循环
                    break;
                }
            }

            //判断newMap是否等于null
            if (newMap == null) {
                //说明从orderList集合中取出的区间不在数据库取出的集合unOrderList中
                newMap = new HashMap();
                //向newMap中添加数据
                newMap.put("count", 0);
                newMap.put("title", title);
                //将newMap放到orderListMaps中
                orderListMaps.add(newMap);
            }
        }

        //判断
        if (CollectionUtils.isEmpty(unOrderList)) {
            //没有数据
            return R.error(ResponseCode.NO_RESPONSE_DATA);
        }
        //3.创建Map集合对象用来组装数据
        Map<String, Object> unOrderMap = new HashMap<String, Object>();
        unOrderMap.put("infos", orderListMaps);
//
        //4.使用DateTime构造方法将上述股票交易时间变为指定日期格式的字符串
        String timeStr = new DateTime(curDate).toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
        //5.将转换后的日期字符串和查询股票信息的集合存放到Map集合中
        unOrderMap.put("time", timeStr);//"time" "2022-01-06 09:55:00"
        //6.将数据返回给web层
        return R.ok(unOrderMap);
    }



    /*
        个股涨跌幅度区间统计功能实现
    */
   /* @Override
    public R<Map<String, Object>> getStockUpDown() {
        //1.获取股票最新一次交易的时间点，使用假的时间数据2022-01-06 09:55:00覆盖当前交易时间
        Date curDate = DateTimeUtil.getLastDate4Stock(DateTime.now()).toDate();
        curDate = DateTime.parse("2022-01-06 09:55:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //2.根据当前时间查询股票信息
        List<Map> unOrderList = stockRtInfoMapper.getStockUpDown(curDate);
        //判断
        if(CollectionUtils.isEmpty(unOrderList)){
            //没有数据
            return R.error(ResponseCode.NO_RESPONSE_DATA);
        }
        //3.创建Map集合对象用来组装数据
        Map<String,Object> unOrderMap = new HashMap<String,Object>();
        unOrderMap.put("infos",unOrderList);
//
        //4.使用DateTime构造方法将上述股票交易时间变为指定日期格式的字符串
        String timeStr = new DateTime(curDate).toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
        //5.将转换后的日期字符串和查询股票信息的集合存放到Map集合中
        unOrderMap.put("time",timeStr);//"time" "2022-01-06 09:55:00"
        //6.将数据返回给web层
        return R.ok(unOrderMap);
    }*/

    /*
     个股分时K线行情功能实现
   */
    @Override
    public R<List<Stock4MinuteDomain>> stockScreenTimeSharing(String code) {
        //实现步骤：
        //1.获取最近最新的交易时间点并使用假数据覆盖当前日期
        DateTime curDateTime = DateTimeUtil.getLastDate4Stock(DateTime.now());
        Date curDate = curDateTime.toDate();
        //TODO:后期删除
        curDate = DateTime.parse("2021-12-30 14:30:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //2.获取最近有效时间点对应的开盘日期并使用假数据覆盖当前日期
        Date openDate = DateTimeUtil.getOpenDate(curDateTime).toDate();
        //TODO:后期删除
        openDate = DateTime.parse("2021-12-30 09:30:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //3.根据股票编码code和日期范围查询单个个股的分时行情数据
        List<Stock4MinuteDomain> list = stockRtInfoMapper.stockScreenTimeSharing(code,openDate,curDate);
        //4.判断上述查询的集合是否为空，如果集合为空，创建集合对象
        if(CollectionUtils.isEmpty(list)){
            list=new ArrayList<>();
        }
        //5.返回响应数据
        return R.ok(list);
    }

    /*
       个股日K线详情功能实现
   */
    @Override
    public R<List<Stock4EvrDayDomain>> getDayKLinData(String code) {
        //1.获取截止时间即股票最新交易时间
        DateTime curDateTime = DateTimeUtil.getLastDate4Stock(DateTime.now());
        Date curDate = curDateTime.toDate();
        //2.根据上述获取最新交易时间基础上使用DateTime对象调用minusMonths方法在现在的时间上减去10个月
        //说明：假设现在时间是2023-02-05日，在该日期上减10个月 2022-04-05
        DateTime startDateTime = curDateTime.minusMonths(10);
        Date startDate = startDateTime.toDate();
        //3.根据个股编码和上述获取的截止时间和开始时间调用mapper接口获取查询的集合信息
        List<Stock4EvrDayDomain> list = stockRtInfoMapper.getDayKLinData(code,startDate,curDate);
        if(CollectionUtils.isEmpty(list)){
            list=new ArrayList<>();
        }
        //4.将查询的数据响应给web层
        return R.ok(list);
    }


}
