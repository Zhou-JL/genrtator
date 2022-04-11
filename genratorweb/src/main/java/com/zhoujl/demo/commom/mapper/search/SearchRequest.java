package com.zhoujl.demo.commom.mapper.search;


import com.zhoujl.demo.utils.StringUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**

 */
@Data
public class SearchRequest<T> implements Serializable {

    @ApiModelProperty("查询过滤扩展条件,一个Map对象")
    private HashMap<String, Object> filter = new HashMap();
    @ApiModelProperty("查询关键字，模糊查询实现")
    private String searchKey;
    @ApiModelProperty("排序条件")
    private String orderBy;
    private T serachBean;
    public SearchRequest() {
    }

    public String getSearchKey() {
        return this.searchKey;
    }

    public void setSearchKey(String searchKey) {
        searchKey = StringUtils.trim(searchKey);
        this.searchKey = StringUtils.sqlStringFilter(searchKey);
    }

    public HashMap<String, Object> getFilter() {
        return this.filter;
    }

    public void setFilter(HashMap<String, Object> filter) {
        this.sqlStringFilter(filter);
        this.filter = filter;
    }

    public String getOrderBy() {
        return this.orderBy;
    }

    public void setOrderBy(String orderBy) {
        orderBy = StringUtils.trim(orderBy);
        this.orderBy = StringUtils.sqlStringFilter(orderBy);
    }

    public void addMap(Map<String, Object> map) {
        if (this.getFilter() == null) {
            this.setFilter(new HashMap());
        }

        this.sqlStringFilter(map);
        this.getFilter().putAll(map);
    }

    private void sqlStringFilter(Map<String, Object> map) {
        if (map != null) {
            Map<String, Object> replaceMap = new HashMap();
            Iterator var3 = map.entrySet().iterator();

            while(var3.hasNext()) {
                Map.Entry<String, Object> item = (Map.Entry)var3.next();
                Object value = item.getValue();
                if (value instanceof String) {
                    value = StringUtils.sqlStringFilter((String)value);
                    replaceMap.put(item.getKey(), value);
                }
            }

            map.putAll(replaceMap);
        }
    }

    public void andEqualTo(String propertyName, Object value) {
        if (propertyName != null && value != null) {
            if (this.getFilter() == null) {
                this.setFilter(new HashMap());
            }

            if (value instanceof String) {
                value = StringUtils.sqlStringFilter((String)value);
            }

            this.getFilter().put(propertyName, value);
        }
    }

    public void andEqualToIfAbsent(String propertyName, Object value) {
        if (propertyName != null && value != null) {
            if (this.getFilter() == null) {
                this.setFilter(new HashMap());
            }

            if (value instanceof String) {
                value = StringUtils.sqlStringFilter((String)value);
            }

            this.getFilter().putIfAbsent(propertyName, value);
        }
    }

    public Object get(String argName) {
        return this.filter == null ? null : this.filter.get(argName);
    }

    public String toString() {
        return String.format("SearchRequest [filter=%s, searchKey=%s, orderBy=%s, toString()=%s]", this.filter, this.searchKey, this.orderBy, super.toString());
    }








}
