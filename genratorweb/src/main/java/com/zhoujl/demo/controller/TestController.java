package com.zhoujl.demo.controller;

import com.zhoujl.demo.commom.mapper.search.PagedList;
import com.zhoujl.demo.commom.mapper.search.PagedSearchRequest;
import com.zhoujl.demo.entity.Menu;
import com.zhoujl.demo.service.MenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/test")
@Slf4j
@Api(tags = "测试—TestController")
public class TestController {



    @Autowired
    MenuService menuService;

    //测试分页

    @PostMapping("t1")
    @ApiOperation(value = "测试分页")
    public PagedList<Menu> getPaged(@RequestBody PagedSearchRequest<Menu> pagedSearchRequest){
        log.info("===================");
        return menuService.getPagedValue(pagedSearchRequest);
    }



}
