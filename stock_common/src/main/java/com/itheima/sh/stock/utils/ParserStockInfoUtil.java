package com.itheima.sh.stock.utils;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.itheima.sh.stock.pojo.entity.StockBlockRtInfo;
import com.itheima.sh.stock.pojo.entity.StockMarketIndexInfo;
import com.itheima.sh.stock.pojo.entity.StockOuterMarketIndexInfo;
import com.itheima.sh.stock.pojo.entity.StockRtInfo;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author by itheima
 * @Date 2021/12/28
 * @Description
 */
public class ParserStockInfoUtil {
    //向数据表添加数据的时候，保证id唯一，使用IdWorker获取唯一的id向数据表插入数据
    public ParserStockInfoUtil(IdWorker idWorker) {
        this.idWorker = idWorker;
    }

    private IdWorker idWorker;

    /**
     * @param stockStr 大盘 股票 实时拉取原始数据(js格式解析)
     *                 var hq_str_sh000001="上证指数,3358.9338,3361.5177,3398.6161,3417.0085,3358.9338,0,0,
     *                 381243178,510307202948,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
     *                 2022-06-30,15:30:39,00,";
     * @param type     1:国内大盘 2.国外大盘 3.A股个股
     *                 上述是我们这里一个规范，因此我们将上述的数字1 2 3抽取到工具类
     *                 ParseType中了
     *                 public static final int INNER=1; 国内大盘标识
     *                 public static final int OUTER=2; 国外大盘标识
     *                 public static final int ASHARE=3; A股标识
     * @return 解析后的数据
     */
    public List parser4StockOrMarketInfo(String stockStr, Integer type) {
        //收集封装数据
        List<Object> datas = new ArrayList<>();
        //合法判断
        if (Strings.isNullOrEmpty(stockStr)) {
            //返回空数组
            return datas;
        }
        //定义正则 第一组：匹配任意非空字符串  第二组：匹配任意除了换行符的字符串包括名称中出现空格的数据
        String reg = "var hq_str_(.+)=\"(.+)\";";
        //编译正则，获取正则对象
        Pattern pattern = Pattern.compile(reg);
        //获取正则匹配器
        Matcher matcher = pattern.matcher(stockStr);
        while (matcher.find()) {
            //解析国内股票大盘 ParseType.INNER值是1
            if (type == ParseType.INNER) {
                StockMarketIndexInfo info = parser4InnerStockMarket(matcher.group(1), matcher.group(2));
                datas.add(info);
            }
            //国外大盘 ParseType.INNER值是2
            if (type == ParseType.OUTER) {
                StockOuterMarketIndexInfo info = parser4OuterStockMarket(matcher.group(1), matcher.group(2));
                datas.add(info);
            }
            //国内A股信息 ParseType.ASHARE值是3
            if (type == ParseType.ASHARE) {
                StockRtInfo info = parser4StockRtInfo(matcher.group(1), matcher.group(2));
                datas.add(info);
            }
        }
        return datas;
    }

    /**
     * 解析国内A股数据
     * var hq_str_sh601006="大秦铁路, 27.55, 27.25, 26.91, 27.55, 26.20, 26.91, 26.92,22114263, 589824680, 4695, 26.91,
     * 57590, 26.90, 14700, 26.89, 14300,26.88, 15100, 26.87, 3100, 26.92, 8900, 26.93, 14230, 26.94, 25150, 26.95,
     * 15220, 26.96, 2008-01-11, 15:05:32";
     * @param stockCode 股票ID  sh601006
     * @param otherInfo 股票其它信息，以逗号间隔
     *        "大秦铁路, 27.55, 27.25, 26.91, 27.55, 26.20, 26.91, 26.92,22114263, 589824680, 4695, 26.91,
     *        57590, 26.90, 14700, 26.89, 14300,26.88, 15100, 26.87, 3100, 26.92, 8900, 26.93, 14230, 26.94, 25150,
     *        26.95,15220, 26.96, 2008-01-11, 15:05:32"
     * @return
     */
    private StockRtInfo parser4StockRtInfo(String stockCode, String otherInfo) {
        ////去除股票sz或者sh前缀 shxxxx。因为数据表stock_business的stock_code的股票编码值没有sz或者sh
        //例如"sz000019"===>stockCode.substring(2)===>截取后的内容是"000019"
        stockCode = stockCode.substring(2);
        /*
            切割后的数组数据：
                others={"大秦铁路","27.55","27.25","26.91","27.55","26.20",..}
                截取后的数据对应的内容如下：
                0：”大秦铁路”，股票名字；
                1：”27.55″，今日开盘价；
                2：”27.25″，昨日收盘价；
                3：”26.91″，当前价格；
                4：”27.55″，今日最高价；
                5：”26.20″，今日最低价；
                8：”22114263″，成交的股票数，由于股票交易以一百股为基本单位，所以在使用时，通常把该值除以一百；
                9：”589824680″，成交金额，单位为“元”，为了一目了然，通常以“万元”为成交金额的单位，所以通常把该值除以一万；
                30：”2008-01-11″，日期；
                31：”15:05:32″，时间；
         */
        String[] others = otherInfo.split(",");
        //个股股票名称 "大秦铁路"
        String stockName = others[0];
        //今日开盘价 27.55
        BigDecimal openPrice = new BigDecimal(others[1]);
        //昨日收盘价 27.25
        BigDecimal preClosePrice = new BigDecimal(others[2]);
        //当前价格 26.91
        BigDecimal currentPrice = new BigDecimal(others[3]);
        //今日最高价额 27.55
        BigDecimal maxPrice = new BigDecimal(others[4]);
        //今日最低价额 26.20
        BigDecimal minPrice = new BigDecimal(others[5]);
        //成交量 22114263
        Long tradeAmount = Long.valueOf(others[8]);
        //成金额 589824680
        BigDecimal tradeVol = new BigDecimal(others[9]);
        //当前日期 2008-01-11 15:05:32
        Date curDateTime = DateTimeUtil.getDateTimeWithoutSecond(others[30] + " " + others[31]).toDate();
        //Date currentTime = DateTime.parse(others[30] + " " + others[31], DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        StockRtInfo stockRtInfo = StockRtInfo.builder()
                .id(idWorker.nextId())
                .stockCode(stockCode)
                .stockName(stockName)
                .openPrice(openPrice)
                .preClosePrice(preClosePrice)
                .curPrice(currentPrice)
                .maxPrice(maxPrice)
                .minPrice(minPrice)
                .tradeAmount(tradeAmount)
                .tradeVolume(tradeVol)
                .curTime(curDateTime)
                .build();
        return stockRtInfo;
    }

    /**
     * 解析国外大盘数据
     *
     * @param marketCode 大盘ID
     * @param otherInfo  大盘其它信息，以逗号间隔
     * @return
     */
    private StockOuterMarketIndexInfo parser4OuterStockMarket(String marketCode, String otherInfo) {
        //其他信息
        String[] others = otherInfo.split(",");
        //大盘名称
        String marketName = others[0];
        //大盘点数
        BigDecimal curPoint = new BigDecimal(others[1]);
        //涨跌值
        BigDecimal upDown = new BigDecimal(others[2]);
        //涨幅
        BigDecimal rose = new BigDecimal(others[3]);
        //获取当前时间
        Date now = DateTimeUtil.getDateTimeWithoutSecond(DateTime.now()).toDate();
        //组装实体对象
        StockOuterMarketIndexInfo smi = StockOuterMarketIndexInfo.builder()
                .id(idWorker.nextId())
                .marketCode(marketCode)
                .curPoint(curPoint)
                .updown(upDown)
                .rose(rose)
                .curTime(now)
                .build();
        return smi;
    }

    /**
     * 解析国内大盘数据
     *
     * @param marketCode 大盘ID
     * @param otherInfo  大盘其它信息，以逗号间隔
     * @return
     */
    private StockMarketIndexInfo parser4InnerStockMarket(String marketCode, String otherInfo) {
        //其他信息
        String[] splitArr = otherInfo.split(",");
        //大盘名称
        String marketName = splitArr[0];
        //获取当前大盘的点数
        BigDecimal openPoint = new BigDecimal(splitArr[1]);
        //获取大盘的涨跌值
        BigDecimal preClosePoint = new BigDecimal(splitArr[2]);
        //获取大盘的涨幅
        BigDecimal curPoint = new BigDecimal(splitArr[3]);
        //获取大盘最高点
        BigDecimal maxPoint = new BigDecimal(splitArr[4]);
        //获取大盘的涨幅
        BigDecimal minPoint = new BigDecimal(splitArr[5]);
        //获取成交量
        Long tradeAmt = Long.valueOf(splitArr[8]);
        //获取成交金额
        BigDecimal tradeVol = new BigDecimal(splitArr[9]);
        //时间
        Date curTime = DateTimeUtil.getDateTimeWithoutSecond(splitArr[30] + " " + splitArr[31]).toDate();
        //组装实体对象
        StockMarketIndexInfo smi = StockMarketIndexInfo.builder()
                .id(idWorker.nextId())
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
        return smi;
    }


    /**
     * 转化板块数据获取集合信息
     *
     * @param stockStr
     * @return
     */
    public List<StockBlockRtInfo> parse4StockBlock(String stockStr) {
        if (Strings.isNullOrEmpty(stockStr) || !stockStr.contains("=")) {
            return Collections.emptyList();
        }
        String jsonStr = stockStr.substring(stockStr.indexOf("=") + 1);
        HashMap mapInfo = new Gson().fromJson(jsonStr, HashMap.class);
        System.out.println(mapInfo);
        Collection values = mapInfo.values();
        List<StockBlockRtInfo> collect = (List<StockBlockRtInfo>) mapInfo.values().stream().map(restStr -> {
            String infos = (String) restStr;
            String[] infoArray = infos.split(",");
            //板块编码
            String label = infoArray[0];
            //板块行业
            String blockName = infoArray[1];
            //板块公司数量
            Integer companyNum = Integer.valueOf(infoArray[2]);
            //均价
            BigDecimal avgPrice = new BigDecimal(infoArray[3]);
            //涨跌幅度
            BigDecimal priceLimit = new BigDecimal(infoArray[5]);
            //涨跌率
            //BigDecimal updownRate=new BigDecimal(infoArray[5]);
            //总成交量
            Long tradeAmount = Long.valueOf(infoArray[6]);
            //总成交金额
            BigDecimal tradeVolume = new BigDecimal(infoArray[7]);
            //当前日期
            Date now = DateTimeUtil.getDateTimeWithoutSecond(DateTime.now()).toDate();
            //构建板块信息对象
            StockBlockRtInfo blockRtInfo = StockBlockRtInfo.builder().id(idWorker.nextId()).label(label)
                    .blockName(blockName).companyNum(companyNum).avgPrice(avgPrice).curTime(now)
                    .updownRate(priceLimit).tradeAmount(tradeAmount).tradeVolume(tradeVolume)
                    .build();
            return blockRtInfo;
        }).collect(Collectors.toList());
        return collect;
    }
}
