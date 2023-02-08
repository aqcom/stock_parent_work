package com.itheima.sh.stock.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author by itheima
 * @Date 2021/12/24
 * @Description 登录后响应前端的vo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRespVo {
    /**
     * 用户ID
     * @JsonSerialize(using = ToStringSerializer.class)放在实体类的字段上可以把整数类型数据转换成String类型，
     * 防止整数类型数据过长，前端拿到数据变成00000
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 电话
     */
    private String phone;
    /**
     * 用户名
     */
    private String username;
    /**
     * 昵称
     */
    private String nickName;

}
