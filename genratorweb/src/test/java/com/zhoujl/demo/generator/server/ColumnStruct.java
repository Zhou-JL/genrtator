package com.zhoujl.demo.generator.server;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColumnStruct {

    private String columnName;//字段名称
    private String dataType;//字段类型
    private String isNullable;//字段是否非空
    private String columnKey;//字段主键
    private String columnComment;//字段注释



    @Override
    public String toString() {
        return "ColumnStruct [columnName = " + columnName + ", dataType="
                + dataType + ", columnKey = "+ columnKey +", columnComment = " + columnComment + "]";
    }








}
