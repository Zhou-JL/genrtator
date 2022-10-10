package com.zhoujl.demo.controller;

import com.zhoujl.demo.commom.mapper.search.PagedList;
import com.zhoujl.demo.commom.mapper.search.PagedSearchRequest;
import com.zhoujl.demo.entity.Menu;
import com.zhoujl.demo.service.MenuService;
import com.zhoujl.demo.utils.IdGeneratorSnowFlake;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.IdGenerator;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@RestController
@RequestMapping("/test")
@Slf4j
@Api(tags = "测试—TestController")
public class TestController {



    @Autowired
    MenuService menuService;

    @Autowired
    private IdGeneratorSnowFlake idGeneratorSnowFlake;

    //测试分页

    @PostMapping("t1")
    @ApiOperation(value = "测试分页")
    public PagedList<Menu> getPaged(@RequestBody PagedSearchRequest<Menu> pagedSearchRequest){
        log.info("===================");
        return menuService.getPagedValue(pagedSearchRequest);
    }



    //雪花算法生成全局唯一Id
    @PostMapping("snowflake")
    @ApiOperation(value = "测试雪花算法生成全局唯一Id")
    public String getUnionId(){
        ExecutorService threadPool = Executors.newFixedThreadPool(5);
        for (int i = 1; i<=20; i++){
            //用线程池的方式创建 20 个 id,相当于20个人去拥有5个办理窗口的银行办理业务
            threadPool.submit(()->{
                System.out.println(idGeneratorSnowFlake.snowflakeTd());
            });
        }
        threadPool.shutdown();
        return "ok";
    }

}
