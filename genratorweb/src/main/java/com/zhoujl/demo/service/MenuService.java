package com.zhoujl.demo.service;


import com.zhoujl.demo.commom.mapper.search.PagedList;
import com.zhoujl.demo.commom.mapper.search.PagedSearchRequest;
import com.zhoujl.demo.entity.Menu;

/**
 * @Author: zjl
 * @company: 北京汉唐智创科技有限公司
 * @time: 2022-3-21 17:55
 * @see: com.zhoujl.demo.rpcservice.service
 * @Version: 1.0
 */
public interface MenuService {

    //分页查询
    PagedList<Menu> getPagedValue(PagedSearchRequest<Menu> pagedSearchRequest);
}
