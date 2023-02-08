package com.itheima.sh.stock;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.jupiter.api.Test;

import java.util.Date;

/**
 * @author by itheima
 * @Date 2022/1/9
 * @Description
 */
//@SpringBootTest(classes = CommonConfig.class)
//@SpringBootTest({})
public class TestJodeDate {

    @Test
    public void testJode(){
        /**
         *  TODO:DateTime属于org.joda.time包下的，我们在stock_common的pom.xml
         *   已经导入依赖：
         *          jode日期插件
         *         <dependency>
         *             <groupId>joda-time</groupId>
         *             <artifactId>joda-time</artifactId>
         *         </dependency>
         */
        //1. DateTime.now()表示获取当前系统时间
        DateTime now = DateTime.now();
        System.out.println("now = " + now);
        //获取指定时间下最近的股票有效交易时间
        //2.withDate(2022, 1, 9)方法表示更改当前系统时间为指定的时间2022-01-09
        DateTime target = DateTime.now().withDate(2022, 1, 9);
        //3.更改当前系统时间为2022-1-10为上午9:00
        //public DateTime withHourOfDay(int var1)表示一天的第几小时
        //public DateTime withMinuteOfHour(int var1){}表示一小时的第几分钟
        target= DateTime.now().withDate(2022, 1, 10).withHourOfDay(9).withMinuteOfHour(0);
        //String leastDateTime = DateTimeUtil.getLastDateString4Stock(target);
        //System.out.println(leastDateTime);
    }

    @Test
    public void testApi(){
        //获取jode下的当前系统时间
        DateTime now = DateTime.now();
        //日期后退指定的时间 now.plusDays(1)表示天数+1
        DateTime plusDay = now.plusDays(1);
        System.out.println(plusDay);
        //前推指定的时间 now.minusDays(5)表示当前系统时间天数-5
        DateTime preDate = now.minusDays(5);
        System.out.println(preDate);
        //随意指定日期 now.withMonthOfYear(8)表示修改当前系统时间的月份变为8月份
        DateTime dateTime = now.withMonthOfYear(8);
        System.out.println(dateTime);
        //转java中的date
        Date date = dateTime.toDate();
        System.out.println(date);
        //日期格式化
        String strDate = dateTime.toString(DateTimeFormat.forPattern("yyyy年MM月ddHHmmss"));
        System.out.println(strDate);
    }
}
