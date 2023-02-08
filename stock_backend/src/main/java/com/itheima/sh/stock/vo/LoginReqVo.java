package com.itheima.sh.stock.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author by itheima
 * @Date 2021/12/30
 * @Description 登录请求vo
 */
@Data
//使用swagger注解修饰实体类
@ApiModel("保存用户登录请求信息的实体类")
public class LoginReqVo {
    /**
     * 用户名
     */
    @ApiModelProperty("用户名")
    private String username;
    /**
     * 密码
     */
    @ApiModelProperty("密码")
    private String password;
    /**
     * 验证码
     */
    @ApiModelProperty("验证码")
    private String code;

    /**
     * 存入redis的随机码
     * 说明：根据获取前端的rkey值到redis中获取验证码
     */
    @ApiModelProperty("存入redis的随机码")
    private String rkey;
}
