package com.itheima.sh.stock;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class PwdTest {
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     *底层原理：
     *      * 从密文中获取盐值（随机码，参与密文生成的运算）后，利用盐值与明文密码进行加密得到密文，
     *      * 这个密文与输入的密文等值匹配
     */
    @Test
    public void testPwd() {
        String pwd = "1234";//明文
        //加密  $2a$10$ys/4NwfUVHmFEJZjFW1ZkOHMS5DeRiisfHcdi4O/G1xB/LA9iivV.
        String encode = passwordEncoder.encode(pwd);//encode表示密文
        System.out.println(encode);
        /*
            boolean matches(明文, 密文);
            matches()匹配明文密码和加密后密码是否匹配，如果匹配，返回true，否则false
            just test
         */
        boolean flag = passwordEncoder.matches(pwd, "$2a$10$ys/4NwfUVHmFEJZjFW1ZkOHMS5DeRiisfHcdi4O/G1xB/LA9iivV.");
        System.out.println(flag);
    }
}
