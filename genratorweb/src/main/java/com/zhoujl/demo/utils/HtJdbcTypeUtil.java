package com.zhoujl.demo.utils;

/**
 */
public class HtJdbcTypeUtil {
    public static String getJdbcType(String dataType, String columnType){
        String type="";
        if("String".equals(dataType)){
            type="VARCHAR";
        }
        if("BigDecimal".equals(dataType)){
            type="DECIMAL";
        }
        if("boolean".equals(dataType)){
            type="BOOLEAN";
        }
        if("byte".equals(dataType)){
            type="TINYINT";
        }
        if("short".equals(dataType)){
            type="SMALLINT";
        }
        if("int".equals(dataType)){
            type="INTEGER";
        }
        if("Integer".equals(dataType)){
            type="INTEGER";
        }
        if("long".equals(dataType)){
            type="BIGINT";
        }
        if("float".equals(dataType)){
            type="DOUBLE";
        }
        if("double".equals(dataType)){
            type="DOUBLE";
        }
        if("Date".equals(dataType)){
            if("datetime".equals(columnType) || "timestamp".equals(columnType)){
                type="TIMESTAMP";
            }else{
                type="DATE";
            }
        }
        if("Time".equals(dataType)){
            type="TIME";
        }
        if("Timestamp".equals(dataType)){
            type="TIMESTAMP";
        }
        if("Boolean".equals(dataType)){
            type="BIT";
        }

        return type;
    }
}
