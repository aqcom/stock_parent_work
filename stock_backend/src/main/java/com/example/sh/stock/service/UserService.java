package com.example.sh.stock.service;

/**
 * @author by itheima
 * @Date 2022/6/29
 * @Description 定义用户服务接口
 */
public interface UserService {

    /**
     * 根据用户查询用户信息
     * @param userName 用户名称
     * @return
     */
    SysUser getUserByUserName(String userName);
}