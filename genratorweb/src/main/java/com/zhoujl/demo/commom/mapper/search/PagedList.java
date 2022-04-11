package com.zhoujl.demo.commom.mapper.search;



import com.zhoujl.demo.utils.Dto2Entity;
import io.swagger.annotations.ApiModelProperty;
import springfox.documentation.annotations.ApiIgnore;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 */
public class PagedList<E> implements Serializable {
    @ApiModelProperty("数据集合")
    private List<E> rows;
    private static final long serialVersionUID = -564244065133648531L;
    @ApiModelProperty("当前分页索引，从1开始计算")
    private int pageIndex;
    private int totalPageCount;
    @ApiModelProperty("总记录行数")
    private int total;
    private int pageSize;
    private E footResult;

    public PagedList() {
        this.pageIndex = 1;
        this.totalPageCount = 0;
        this.total = 0;
        this.pageSize = 10;
        this.rows = new LinkedList();
    }

    public PagedList(Collection<? extends E> c) {
        this.pageIndex = 1;
        this.totalPageCount = 0;
        this.total = 0;
        this.pageSize = 10;
        this.rows = new LinkedList(c);
    }

    public PagedList(Collection<? extends E> c, Pagination pagination) {
        this(c);
        this.setPageIndex(pagination.getPageIndex());
        this.setPageSize(pagination.getPageSize());
        this.setTotalItemCount(pagination.getTotal());
    }

    public <T> PagedList<T> toViewPaged(Class<T> entityClass) {
        PagedList<T> result = new PagedList();
        result.setTotalItemCount(this.getTotalItemCount());
        result.setPageIndex(this.getPageIndex());
        result.setPageSize(this.getPageSize());
        result.setRows(Dto2Entity.transalteList(this.rows, entityClass));
        return result;
    }

    public void setTotalItemCount(int count) {
        this.setTotal(count);
    }

    public int getTotalItemCount() {
        return this.getTotal();
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) {
        if (pageSize < 2) {
            pageSize = 10;
        }

        this.pageSize = pageSize;
    }

    public int getPageIndex() {
        return this.pageIndex < 1 ? 1 : this.pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        if (pageIndex < 1) {
            pageIndex = 1;
        }

        this.pageIndex = pageIndex;
    }

    @ApiIgnore
    public int getTotalPageCount() {
        return this.totalPageCount;
    }

    public void setTotalPageCount(int totalPageCount) {
        this.totalPageCount = totalPageCount;
    }

    public List<E> getRows() {
        return this.rows;
    }

    public void setRows(List<E> list) {
        this.rows.clear();
        if (list != null) {
            this.rows.addAll(list);
        }

    }

    public void add(E item) {
        this.rows.add(item);
    }

    public E get(int index) {
        return this.rows.get(index);
    }

    public int getTotal() {
        return this.total < this.getRows().size() ? this.getRows().size() : this.total;
    }

    public void setTotal(int total) {
        this.total = total;
        this.totalPageCount = total % this.pageSize == 0 ? total / this.pageSize : total / this.pageSize + 1;
    }

    @ApiIgnore
    public E getFootResult() {
        return this.footResult;
    }

    public void setFootResult(E footResult) {
        this.footResult = footResult;
    }



}
