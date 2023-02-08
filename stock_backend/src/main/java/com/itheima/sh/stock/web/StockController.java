package com.itheima.sh.stock.web;

import com.itheima.sh.stock.pojo.domain.InnerMarketDomain;
import com.itheima.sh.stock.pojo.domain.Stock4EvrDayDomain;
import com.itheima.sh.stock.pojo.domain.Stock4MinuteDomain;
import com.itheima.sh.stock.pojo.vo.PageResult;
import com.itheima.sh.stock.pojo.vo.R;
import com.itheima.sh.stock.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quot")
public class StockController {

    @Autowired
    private StockService stockService;

    /*
        定义方法查询国内大盘数据
     */
    @GetMapping("/index/all")
    public R<List<InnerMarketDomain>> innerIndexAll(){
        //使用业务层对象调用查询国内大盘数据的方法
        R<List<InnerMarketDomain>> r = stockService.innerIndexAll();
        //返回给前端
        return r;
    }

     /*
        定义方法查询国内个股数据并按照涨幅降序排序
     */
     @GetMapping("/stock/all")
     public R<PageResult> getStockPageInfo(Integer page, Integer pageSize){
        //使用业务层对象调用方法分页查询个股信息
         return  stockService.getStockPageInfo(page,pageSize);
     }

     /*
        功能描述：统计沪深两市T日(当前股票交易日)每分钟达到涨跌停股票的数据
      */
     @GetMapping("/stock/updown/count")
     public R<Map<String,List<Map<String,Object>>>> getStockUpdownCount(){
         //使用业务层对象调用方法统计沪深两市T日(当前股票交易日)每分钟达到涨跌停股票的数据
         return  stockService.getStockUpdownCount();
     }

     /*
        涨幅信息数据导出功能实现
      */
     @GetMapping("/stock/export")
     public void stockExport(HttpServletResponse response,Integer page,Integer pageSize){
         //使用业务层对象调用方法实现涨幅信息数据导出功能
         stockService.stockExport(response,page,pageSize);
     }

    /*
      成交量对比功能实现
    */
    @GetMapping("/stock/tradevol")
    public R<Map<String,List<Map<String,Object>>>> stockTradeVol4InnerMarket(){
        //使用业务层对象调用方法实现成交量对比功能
        return stockService.stockTradeVol4InnerMarket();
    }

    /*
        个股涨跌幅度区间统计功能实现
    */
    @GetMapping("/stock/updown")
    public R<Map<String,Object>> getStockUpDown(){
        //使用业务层对象调用方法实现个股涨跌幅度区间统计功能
        return stockService.getStockUpDown();
    }

    /*
       个股分时K线行情功能实现
   */
    @GetMapping("/stock/screen/time-sharing")
    public R<List<Stock4MinuteDomain>> stockScreenTimeSharing(String code){
        //使用业务层对象调用方法实现个股分时K线行情功能实现
        return stockService.stockScreenTimeSharing(code);
    }

    /*
       个股日K线详情功能实现
   */
    @GetMapping("/stock/screen/dkline")
    public R<List<Stock4EvrDayDomain>> getDayKLinData(String code){
        //使用业务层对象调用方法实现个股日K线详情功能实现
        return stockService.getDayKLinData(code);
    }
}
