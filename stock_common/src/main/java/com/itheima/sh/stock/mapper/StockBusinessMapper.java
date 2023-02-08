package com.itheima.sh.stock.mapper;

import com.itheima.sh.stock.pojo.entity.StockBusiness;

import java.util.List;

/**
* @author tians
* @description 针对表【stock_business(主营业务表)】的数据库操作Mapper
* @createDate 2023-01-30 14:52:29
* @Entity com.itheima.sh.stock.pojo.entity.StockBusiness
*/
public interface StockBusinessMapper {

    int deleteByPrimaryKey(Long id);

    int insert(StockBusiness record);

    int insertSelective(StockBusiness record);

    StockBusiness selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StockBusiness record);

    int updateByPrimaryKey(StockBusiness record);

    /**
     * 测试获取个股编码集合
     * @return
     */
    List<String> getStockCodes();

}
