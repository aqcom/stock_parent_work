package com.itheima.sh.stock.mapper;

import com.itheima.sh.stock.pojo.domain.Stock4EvrDayDomain;
import com.itheima.sh.stock.pojo.domain.Stock4MinuteDomain;
import com.itheima.sh.stock.pojo.domain.StockUpdownDomain;
import com.itheima.sh.stock.pojo.entity.StockRtInfo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
* @author tians
* @description 针对表【stock_rt_info(个股详情信息表)】的数据库操作Mapper
* @createDate 2023-01-30 14:52:29
* @Entity com.itheima.sh.stock.pojo.entity.StockRtInfo
*/
public interface StockRtInfoMapper {

    int deleteByPrimaryKey(Long id);

    int insert(StockRtInfo record);

    int insertSelective(StockRtInfo record);

    StockRtInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StockRtInfo record);

    int updateByPrimaryKey(StockRtInfo record);
    /*
     定义方法查询国内个股数据并按照涨幅降序排序
   */
    List<StockUpdownDomain> getStockPageInfo(@Param("curDate") Date curDate);
    //查询涨跌停   openDate 开盘时间  curDate 当前时间  flag 表示标记 1：涨停数据 0：跌停
    List<Map<String, Object>> getStockUpdownCount(@Param("openDate") Date openDate,
                                                  @Param("curDate") Date curDate,
                                                  @Param("flag") int flag);
    /*
        个股涨跌幅度区间统计功能实现
    */
    List<Map> getStockUpDown(@Param("curDate") Date curDate);
    /*
        个股分时K线行情功能实现
    */
    List<Stock4MinuteDomain> stockScreenTimeSharing(@Param("code") String code,
                                                    @Param("openDate") Date openDate,
                                                    @Param("curDate") Date curDate);
    /*
      个股日K线详情功能实现
  */
    List<Stock4EvrDayDomain> getDayKLinData(@Param("code") String code,
                                            @Param("startDate") Date startDate,
                                            @Param("curDate") Date curDate);
    /*
        批量向stock_rt_info表中插入股票实时数据
     */
    void insertBach(@Param("list") List<StockRtInfo> list);
}
