package com.itheima.sh.stock.mapper;

import com.itheima.sh.stock.pojo.domain.InnerMarketDomain;
import com.itheima.sh.stock.pojo.entity.StockMarketIndexInfo;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
* @author tians
* @description 针对表【stock_market_index_info(国内大盘数据详情表)】的数据库操作Mapper
* @createDate 2023-01-30 14:52:29
* @Entity com.itheima.sh.stock.pojo.entity.StockMarketIndexInfo
*/
public interface StockMarketIndexInfoMapper {

    int deleteByPrimaryKey(Long id);

    int insert(StockMarketIndexInfo record);

    int insertSelective(StockMarketIndexInfo record);

    StockMarketIndexInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StockMarketIndexInfo record);

    int updateByPrimaryKey(StockMarketIndexInfo record);
    //定义方法查询国内大盘数据
    List<InnerMarketDomain> innerIndexAll(@Param("curDate") Date curDate, @Param("innerList")List<String> innerList);
    /*
    成交量对比功能实现
    */
    List<Map<String, Object>> stockTradeVol4InnerMarket(@Param("innerMarketCodeList") List<String> innerMarketCodeList,
                                                        @Param("curDate4T") Date curDate4T,
                                                        @Param("openDate4T") Date openDate4T);
    /*
        向大盘批量插入数据
     */
    void insertBach(@Param("marketIndexInfoList") ArrayList<StockMarketIndexInfo> marketIndexInfoList);
}
