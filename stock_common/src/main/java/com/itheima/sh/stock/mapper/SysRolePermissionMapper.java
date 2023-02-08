package com.itheima.sh.stock.mapper;

import com.itheima.sh.stock.pojo.entity.SysRolePermission;

/**
* @author tians
* @description 针对表【sys_role_permission(角色权限表)】的数据库操作Mapper
* @createDate 2023-01-30 14:52:29
* @Entity com.itheima.sh.stock.pojo.entity.SysRolePermission
*/
public interface SysRolePermissionMapper {

    int deleteByPrimaryKey(Long id);

    int insert(SysRolePermission record);

    int insertSelective(SysRolePermission record);

    SysRolePermission selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysRolePermission record);

    int updateByPrimaryKey(SysRolePermission record);

}
