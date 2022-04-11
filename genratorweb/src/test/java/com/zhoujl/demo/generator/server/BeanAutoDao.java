package com.zhoujl.demo.generator.server;


public interface BeanAutoDao {

    //通过表名、字段名称、字段类型创建Bean实体
    public boolean createBean(String headValue, String excludeTableName);
}
