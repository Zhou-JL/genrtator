package com.zhoujl.demo.dao;

import com.zhoujl.demo.commom.mapper.search.Pagination;
import com.zhoujl.demo.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

/**
 * @Author: zjl
 * @company: 北京汉唐智创科技有限公司
 * @time: 2022-3-22 10:11
 * @see: com.zhoujl.demo.dao
 * @Version: 1.0
 */
@Mapper
public interface MenuDao {

    /**
     * 分页查询符合条件的所有记录
     * @Param condition 实体接收参数
     * */
    List<Menu> getPagedValue(@Param("condition") Menu record, @Param("page") Pagination pagination);

    List<Menu> getPagedValue1(@Param("condition") Menu record,@Param("keywords") String keywords, @Param("page") Pagination pagination);

    List<Menu> getPagedValue2(@Param("condition") Menu record,@Param("keywords") String keywords, @Param("page") Pagination pagination,@Param("map") HashMap<String, Object> map);
}
