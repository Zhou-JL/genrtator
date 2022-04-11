package com.zhoujl.demo.generator.server;



import com.zhoujl.demo.utils.HtDataSourceUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GetTablesDaoImpl extends HtDataSourceUtil implements GetTablesDao {

    //获得数据库的所有表名,表注释
    @Override
    public List<Map<String, String>> getTablesName() {
        List tables = new ArrayList();
        String sql = "SELECT table_name, table_comment FROM information_schema.TABLES WHERE table_schema = ";
        ResultSet rs= this.tableSchemaQuery(sql, null);
        try {
            while(rs.next()){
                Map<String, String> tableInfMap = new HashMap<String, String>();
                //将获得的所有表名装进List
                tableInfMap.put("tableName", rs.getString(1));
                tableInfMap.put("tableComment", rs.getString(2));
                tables.add(tableInfMap);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return tables;
    }

    //获得数据表中的字段名称、字段类型
    @Override
    public List getTablesStruct() {
        //获得装有所有表名的List
        List<Map<String, String>> tables = this.getTablesName();
        String sqls = null;
        //装所有的表结构（表名+字段名称+字段类型）
        List tablesStruct = new ArrayList();
        for (int i = 0; i < tables.size(); i++) {
            String tableName = tables.get(i).get("tableName");
            String tableComment = tables.get(i).get("tableComment");
            sqls = "show full columns from  " + tableName;
            ResultSet rs = this.query(sqls);
            //装所有的列结构(字段名称+字段类型)
            List list = new ArrayList();
            try {
                while(rs.next()){
                    ColumnStruct cs = new ColumnStruct(rs.getString(1), rs.getString(2), rs.getString(4), rs.getString(5), rs.getString(9));
                    //找到一列装进List
                    list.add(cs);
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            //遍历完一张表，封装成对象
            TableStruct ts = new TableStruct(tableName, tableComment, list);
            //将对象（一张表）装进集合
            tablesStruct.add(ts);
        }
        return tablesStruct;
    }

}
