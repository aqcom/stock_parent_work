package com.itheima.sh.stock.easyexcel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author by itheima
 * @Date 2021/12/19
 * @Description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@HeadRowHeight(value = 35) // 表头行高
@ContentRowHeight(value = 25) // 内容行高
@ColumnWidth(value = 50) // 列宽
public class User implements Serializable {
    //设置生成excel表格的表头
    /*
        @ExcelProperty(value = "用户名",index = 0) 用来修饰向表格中写数据的实体类的属性的
            @ExcelProperty注解的常见属性：
                1）value表示表头名
                2）index表示第几列，第一列是0，第二列是1，依次递增

       1. String[] value() default {""}; 可以给value属性赋值来指定表格的表头
     */
    @ExcelProperty(value ={"用户信息", "用户名"},index = 0)
    private String userName;
    @ExcelProperty(value ={"用户信息", "年龄"},index = 1)
    //使用该注解修饰的属性不会被写出到指定的excel表格中
//    @ExcelIgnore
    private Integer age;
    @ExcelProperty(value ={"用户信息", "住址"},index = 2)
    private String address;
    @ExcelProperty(value ={"用户信息", "生日"},index = 3)
    //使用easyexcel中的注解指定向excel中写出的日期格式
    @DateTimeFormat("yyyy/MM/dd HH:mm")
    private Date birthday;
}