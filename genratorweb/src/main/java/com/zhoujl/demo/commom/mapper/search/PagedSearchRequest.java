package com.zhoujl.demo.commom.mapper.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**

 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagedSearchRequest<T> extends SearchRequest<T> {

    private Integer page;
    private Integer rows;

    public void setRows(Integer rows) {
        if (rows <= 0) {
            this.rows = 10;
        }

        this.rows = rows;
    }

    public Pagination toPagination() {
        Pagination pagination = new Pagination(this.rows, this.page);
        pagination.setOrderBy(this.getOrderBy());
        return pagination;
    }

}
