package com.zhoujl.demo.generator.server;

/**
 */
public interface DaoAutoDao {
    //通过表名、字段名称、字段类型创建Dao接口
    public boolean createDao(String headValue, String excludeTableName);
}
