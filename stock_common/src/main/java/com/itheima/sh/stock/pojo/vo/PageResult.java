package com.itheima.sh.stock.pojo.vo;

import com.github.pagehelper.PageInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


/**
 * 分页工具类
 */
@Data
public class PageResult<T> implements Serializable {
    /**
     * 总记录数
     */
    private Long totalRows;

    /**
     * 总页数
     */
    private Integer totalPages;

    /**
     * 当前第几页
     */
    private Integer pageNum;
    /**
     * 每页记录数
     */
    private Integer pageSize;
    /**
     * 当前页记录数
     */
    private Integer size;
    /**
     * 结果集
     */
    private List<T> rows;

    /**
     * 分页数据组装
     * @param pageInfo，属于pagehelper分页插件中的PageInfo类型，PageInfo类具有多个分页的相关属性
     *                  因此我们这里在PageResult构造方法体中将pageInfo部分属性取出赋值给PageResult类
     *                 中的属性
     *   目的：不将 PageInfo实体类对象中的所有分页数据响应给前端
     */
    public PageResult(PageInfo<T> pageInfo) {
        totalRows = pageInfo.getTotal();
        totalPages = pageInfo.getPages();
        pageNum = pageInfo.getPageNum();
        pageSize = pageInfo.getPageSize();
        size = pageInfo.getSize();
        rows = pageInfo.getList();
    }
}
