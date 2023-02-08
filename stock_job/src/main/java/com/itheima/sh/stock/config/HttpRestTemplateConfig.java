package com.itheima.sh.stock.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class HttpRestTemplateConfig {
    //创建方法将采集数据的核心累对象放到IOC容器
    @Bean
    public RestTemplate restTemplate(){
        //返回对象
        return new RestTemplate();
    }
}
