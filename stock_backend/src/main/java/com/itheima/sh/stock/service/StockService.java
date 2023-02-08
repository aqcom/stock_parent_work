package com.itheima.sh.stock.service;

import com.itheima.sh.stock.pojo.domain.InnerMarketDomain;
import com.itheima.sh.stock.pojo.domain.Stock4EvrDayDomain;
import com.itheima.sh.stock.pojo.domain.Stock4MinuteDomain;
import com.itheima.sh.stock.pojo.vo.PageResult;
import com.itheima.sh.stock.pojo.vo.R;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface StockService {
    /*
        定义方法查询国内大盘数据
     */
    R<List<InnerMarketDomain>> innerIndexAll();
    /*
       定义方法查询国内个股数据并按照涨幅降序排序
    */
    R<PageResult> getStockPageInfo(Integer page, Integer pageSize);
    /*
       功能描述：统计沪深两市T日(当前股票交易日)每分钟达到涨跌停股票的数据
     */
    R<Map<String, List<Map<String, Object>>>> getStockUpdownCount();

    /*
        涨幅信息数据导出功能实现
   */
    void stockExport(HttpServletResponse response, Integer page, Integer pageSize);
    /*
     成交量对比功能实现
   */
    R<Map<String, List<Map<String, Object>>>> stockTradeVol4InnerMarket();
    /*
        个股涨跌幅度区间统计功能实现
    */
    R<Map<String, Object>> getStockUpDown();
    /*
      个股分时K线行情功能实现
    */
    R<List<Stock4MinuteDomain>> stockScreenTimeSharing(String code);

    /*
       个股日K线详情功能实现
   */
    R<List<Stock4EvrDayDomain>> getDayKLinData(String code);
}
