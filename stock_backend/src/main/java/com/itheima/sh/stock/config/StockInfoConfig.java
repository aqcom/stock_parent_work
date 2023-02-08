package com.itheima.sh.stock.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author by itheima
 * @Date 2021/12/30
 * @Description
 */
//将StockInfoConfig对象放到SpringIOC容器中
@Configuration
/*
    表示使用配置文件application-stock.yml中前缀为stock的属性的值初始化该bean
    即StockInfoConfig对象中
    定义产生的的bean实例的同名属性在使用时这个定义产生的bean时，
    其属性inner集合中会是{"sh000001","sz399001"}

    @ConfigurationProperties(prefix = "stock") ===》prefix的属性值是
    application-stock.yml配置文件中的：
        stock:
            inner:
            。。。。
      的stock
 */
@ConfigurationProperties(prefix = "stock")
@Data
public class StockInfoConfig {
    //A股大盘ID集合
    private List<String> inner;
    //外盘ID集合
    private List<String> outer;

    //存取涨幅区间的集合
    private List<String> upDownRange;
}
