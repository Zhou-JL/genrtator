package com.zhoujl.demo.generator.server;

/**

 */
public interface MapperXmlAutoDao {
    //通过表名、字段名称、字段类型创建Mapper.xml
    public boolean createMapperXml(String headValue, String excludeTableName);
}
