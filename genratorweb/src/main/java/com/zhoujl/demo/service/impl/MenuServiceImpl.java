package com.zhoujl.demo.service.impl;

import com.zhoujl.demo.commom.mapper.search.PagedList;
import com.zhoujl.demo.commom.mapper.search.PagedSearchRequest;
import com.zhoujl.demo.commom.mapper.search.Pagination;
import com.zhoujl.demo.dao.MenuDao;
import com.zhoujl.demo.entity.Menu;
import com.zhoujl.demo.service.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: zjl
 * @company: 北京汉唐智创科技有限公司
 * @time: 2022-3-21 18:00
 * @see: com.zhoujl.demo.service.impl
 * @Version: 1.0
 */
@Service
@Slf4j
public class MenuServiceImpl implements MenuService {

    @Autowired
    MenuDao menuDao;


    @Override
    public PagedList<Menu> getPagedValue(PagedSearchRequest<Menu> pagedSearchRequest) {
        Pagination pagination = pagedSearchRequest.toPagination();
//        List<Menu> pagedValue = menuDao.getPagedValue(pagedSearchRequest.getSerachBean(), pagination);
//        List<Menu> pagedValue = menuDao.getPagedValue1(pagedSearchRequest.getSerachBean(),pagedSearchRequest.getSearchKey(), pagination);
        List<Menu> pagedValue = menuDao.getPagedValue2(pagedSearchRequest.getSerachBean(),pagedSearchRequest.getSearchKey(), pagination,pagedSearchRequest.getFilter());

        if(pagedValue == null && pagedValue.size() <= 0){
            return null;
        }
        return new PagedList<>(pagedValue, pagination);
    }
}
