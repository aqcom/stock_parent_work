package com.itheima.sh.stock.mapper;

import com.itheima.sh.stock.pojo.entity.SysLog;

/**
* @author tians
* @description 针对表【sys_log(系统日志)】的数据库操作Mapper
* @createDate 2023-01-30 14:52:29
* @Entity com.itheima.sh.stock.pojo.entity.SysLog
*/
public interface SysLogMapper {

    int deleteByPrimaryKey(Long id);

    int insert(SysLog record);

    int insertSelective(SysLog record);

    SysLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysLog record);

    int updateByPrimaryKey(SysLog record);

}
