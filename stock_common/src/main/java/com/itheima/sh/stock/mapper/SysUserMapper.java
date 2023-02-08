package com.itheima.sh.stock.mapper;

import com.itheima.sh.stock.pojo.entity.SysUser;
import org.apache.ibatis.annotations.Param;

/**
* @author tians
* @description 针对表【sys_user(用户表)】的数据库操作Mapper
* @createDate 2023-01-30 14:52:29
* @Entity com.itheima.sh.stock.pojo.entity.SysUser
*/
public interface SysUserMapper {

    int deleteByPrimaryKey(Long id);

    int insert(SysUser record);

    int insertSelective(SysUser record);

    SysUser selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysUser record);

    int updateByPrimaryKey(SysUser record);
    //根据用户名查询用户
    SysUser findUserByUserName(@Param("username") String username);
}
