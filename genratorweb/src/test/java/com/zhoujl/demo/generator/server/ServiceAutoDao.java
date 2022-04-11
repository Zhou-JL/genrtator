package com.zhoujl.demo.generator.server;

/**
 */
public interface ServiceAutoDao {

    //通过表名、字段名称、字段类型创建Service接口
    public boolean createService(String headValue, String excludeTableName);
}
