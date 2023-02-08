package com.itheima.sh.stock;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//扫描mybatis操作数据库的接口
@MapperScan("com.itheima.sh.stock.mapper")
public class StockJobApp {
    public static void main(String[] args) {
        SpringApplication.run(StockJobApp.class, args);
    }
}
