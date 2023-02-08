package com.itheima.sh.stock.service;

import com.itheima.sh.stock.pojo.entity.SysUser;
import com.itheima.sh.stock.pojo.vo.R;
import com.itheima.sh.stock.vo.LoginReqVo;
import com.itheima.sh.stock.vo.LoginRespVo;

import java.util.Map;

public interface UserService {
    //根据用户名查询用户
    SysUser findUserByUserName(String username);
    //用户登录
    R<LoginRespVo> login(LoginReqVo user);
    //生成验证码给浏览器客户端
    R<Map> generateCaptcha();
}
