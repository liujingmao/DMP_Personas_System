package com.imooc.dmp.utils;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Program: dmp
 * Description: 分页工具类
 * Author: Wu
 * Date: 2022/02/09 11:27
 */
@Component
public class PageUtil<T> {
    //分页起始页
    private int page;
    //每页记录数
    private int size;
    //返回的记录集合
    private List<T> data;
    //总记录条数
    private long total;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
