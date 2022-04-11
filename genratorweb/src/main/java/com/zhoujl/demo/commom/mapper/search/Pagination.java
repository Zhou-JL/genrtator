package com.zhoujl.demo.commom.mapper.search;



import com.zhoujl.demo.utils.StringUtils;

import java.io.Serializable;

/**
 */
public class Pagination implements Serializable {
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int DEFAULT_CURRENT_SKIP = 1;
    private int pageSize;
    private int total;
    private int totalPage;
    private int pageIndex;
    private boolean unPagedMark;
    private String orderBy;

    public Pagination() {
        this.pageSize = 10;
        this.pageIndex = 1;
        this.unPagedMark = false;
    }

    public Pagination(int pageSize) {
        this(pageSize, 1);
    }

    public Pagination(int pageSize, int pageIndex) {
        this.pageSize = 10;
        this.pageIndex = 1;
        this.unPagedMark = false;
        if (pageSize <= 0) {
            pageSize = 10;
        }

        this.pageSize = pageSize;
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) {
        if (pageSize <= 0) {
            pageSize = 10;
        }

        this.pageSize = pageSize;
    }

    public int getTotal() {
        return this.total;
    }

    public void setTotal(int total) {
        this.total = total;
        this.totalPage = total % this.pageSize == 0 ? total / this.pageSize : total / this.pageSize + 1;
    }

    public int getPageIndex() {
        return this.pageIndex <= 1 ? 1 : this.pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getTotalPage() {
        return this.totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public String getOrderBy() {
        return this.orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = StringUtils.sqlStringFilter(orderBy);
    }

    public boolean isUnPagedMark() {
        return this.unPagedMark;
    }

    public void setUnPagedMark(boolean unPagedMark) {
        this.unPagedMark = unPagedMark;
    }

    public static Pagination createListArg(int maxCount) {
        Pagination result = new Pagination(maxCount, 1);
        result.setUnPagedMark(true);
        return result;
    }

    public static Pagination createAllArg() {
        Pagination result = new Pagination(2147483647, 1);
        result.setUnPagedMark(true);
        return result;
    }

    public static Pagination createOneArg() {
        Pagination result = new Pagination(1, 1);
        result.setUnPagedMark(true);
        return result;
    }


}
