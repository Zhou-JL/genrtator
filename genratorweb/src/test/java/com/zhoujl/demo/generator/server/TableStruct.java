package com.zhoujl.demo.generator.server;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableStruct {

    private String tableName;//表名
    private String tableComment;//表注释
    private List columns;//所有的列




    @Override
    public String toString() {
        return "TableStruct [tableName=" + tableName + ", columns=" + columns
                + ", tableComment = "+ tableComment +"]";
    }
}
