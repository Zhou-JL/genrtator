package com.zhoujl.demo.generator.server;

import java.util.List;
import java.util.Map;

/**
 *  获取数据表及其结构的dao层接口
 */
public interface GetTablesDao {

    //获得数据库的所有表名
    public List<Map<String, String>> getTablesName();

    //获得数据表中的字段名称、字段类型
    public List getTablesStruct();
}
