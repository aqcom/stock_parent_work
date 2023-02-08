package com.itheima.sh.stock.config;

import com.itheima.sh.stock.utils.IdWorker;
import com.itheima.sh.stock.utils.ParserStockInfoUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/*
    公共配置类
 */
@Configuration
public class CommonConfig {

    /**
     * 密码加密器
     * BCryptPasswordEncoder方法采用SHA-256对密码进行加密
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        //BCryptPasswordEncoder是接口PasswordEncoder实现类
        return new BCryptPasswordEncoder();
    }

    /**
     * 雪花算法生成的id
     */
    @Bean
    public IdWorker getIdWorker(){
        //1 表示机器id  2表示机房id
//        return new IdWorker(1,2);
        return new IdWorker();
    }

    /*
        将解析拉取股票的工具类放到IOC容器中
     */
    @Bean
    public ParserStockInfoUtil parserStockInfoUtil(IdWorker getIdWorker){
        return new ParserStockInfoUtil(getIdWorker);
    }

}
