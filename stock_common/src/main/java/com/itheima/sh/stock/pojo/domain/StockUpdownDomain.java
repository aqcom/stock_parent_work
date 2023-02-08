package com.itheima.sh.stock.pojo.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author by itheima
 * @Date 2022/2/28
 * @Description 股票涨跌信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
//swagger注解
@ApiModel("个股涨幅榜数据封装对象")
@HeadRowHeight(value = 35) // 表头行高
@ContentRowHeight(value = 25) // 内容行高
@ColumnWidth(value = 50) // 列宽
public class StockUpdownDomain {
    @ApiModelProperty("股票编码")
    //将数据导出到excel表格中使用的注解
    //"股票涨幅信息统计表" 表示合并表格的列的名字
    //"股票编码" 表示列名
    //index = 0 表示第一列
    @ExcelProperty(value = {"股票涨幅信息统计表","股票编码"},index = 0)
    private String code;

    @ApiModelProperty("股票名称")
    @ExcelProperty(value = {"股票涨幅信息统计表","股票名称"},index = 1)
    private String name;

    @ApiModelProperty("前收盘价格")
    @ExcelProperty(value = {"股票涨幅信息统计表","前收盘价格"},index = 2)
    private BigDecimal preClosePrice;

    @ApiModelProperty("当前价格")
    @ExcelProperty(value = {"股票涨幅信息统计表","当前价格"},index= 3)
    private BigDecimal tradePrice;

    @ApiModelProperty("涨跌值")
    @ExcelProperty(value = {"股票涨幅信息统计表","涨跌"},index= 4)
    private BigDecimal increase;

    @ApiModelProperty("涨幅")
    @ExcelProperty(value = {"股票涨幅信息统计表","涨幅"},index= 5)
    private BigDecimal upDown;

    @ApiModelProperty("振幅")
    @ExcelProperty(value = {"股票涨幅信息统计表","振幅"},index= 6)
    private BigDecimal amplitude;

    @ApiModelProperty("交易量")
    @ExcelProperty(value = {"股票涨幅信息统计表","交易总量"},index = 7)
    private Long tradeAmt;

    @ApiModelProperty("交易金额")
    @ExcelProperty(value = {"股票涨幅信息统计表","交易总金额"},index = 8)
    private BigDecimal tradeVol;

    /**
     * 日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")//springmvc的注解-》json格式数据
    @ApiModelProperty("交易时间")
    @ExcelProperty(value = {"股票涨幅信息统计表","日期"},index = 9)
    @DateTimeFormat("yyy-MM-dd HH:mm")//easyExcel的注解-》excel
    private Date curDate;
}

