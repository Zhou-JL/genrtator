package com.zhoujl.demo.generator.server;



import com.zhoujl.demo.utils.HtConfigUtil;
import com.zhoujl.demo.utils.HtFileUtil;
import com.zhoujl.demo.utils.HtNameUtil;
import com.zhoujl.demo.utils.StringUtils;

import java.util.List;

/**
 */
public class DaoAutoDaoImpl implements DaoAutoDao {

    //从GetTablesDaoImpl中获得装有所有表结构的List
    GetTablesDao getTables = new GetTablesDaoImpl();
    List<TableStruct> list = getTables.getTablesStruct();

    //通过表名、字段名称、字段类型创建Dao接口
    @Override
    public boolean createDao(String headValue, String excludeTableName) {
        //获得配置文件的参数
        //项目路径
        String projectPath = HtConfigUtil.projectPath;
        //是否生成Dao
        String daoFalg = HtConfigUtil.daoFlag;
        //Dao接口的包名
        String daoPackage = HtConfigUtil.daoPackage;
        //Bean实体类的包名
        String beanPackage = HtConfigUtil.beanPackage;
        if ("true".equals(daoFalg)) {
            //将包名com.xxx.xxx形式，替换成com/xxx/xxx形成
            String daoPath = daoPackage.replace(".", "/");
            //Dao接口的路径
            String path = projectPath + "/src/" + daoPath;
            //遍历装有所有表结构的List
            for (int i = 0; i < list.size(); i++) {
                //文件名
                TableStruct dbTableInfo = list.get(i);
                String dbTableName = dbTableInfo.getTableName();
                String fileName = HtNameUtil.fileName(dbTableName) + "Dao";
                String beanName = HtNameUtil.fileName(dbTableName);
                if(StringUtils.isNotNullOrEmpty(excludeTableName) && (dbTableName.contains(excludeTableName) || dbTableName.equals(excludeTableName)) ){
                    continue;
                }

                String keyDBColumn = "";
                boolean existKey = false;
                String keyColumn = "";
                //获得每个表的所有列结构
                List<ColumnStruct> columns = dbTableInfo.getColumns();
                for (int j = 0; j <columns.size(); j++) {
                    ColumnStruct currentColumn = columns.get(j);
                    //变量名（属性名）
                    String dbColumnName = currentColumn.getColumnName();
                    //属性（变量）名
                    String columnName = HtNameUtil.columnName(dbColumnName);
                    //字段主键
                    String columnKey = currentColumn.getColumnKey();
                    if("PRI".equals(columnKey)){
                        keyDBColumn = dbColumnName;
                        keyColumn = columnName;
                        existKey = true;
                    }
                }

                //(Dao接口）文件内容
                String packageCon = "package " + daoPackage + ";\n\n";
                StringBuffer importCon = new StringBuffer();
                String daoHeadCon = "/**\n" + headValue;
                String className = "@Mapper\npublic interface " + fileName + " extends HtzcMapper<" + beanName + "> {\n\n";
                StringBuffer classCon = new StringBuffer();

                //生成导包内容
                importCon.append("import "+beanPackage+"."+beanName+";\n");
                importCon.append("import com.htzc.financebusiness.common.mapper.HtzcMapper;\n");
                importCon.append("import com.htzc.financebusiness.common.mapper.search.Pagination;\n");
                importCon.append("import org.apache.ibatis.annotations.Param;\n");
                importCon.append("import org.apache.ibatis.annotations.Mapper;\n\n");
                importCon.append("import java.util.List;\n");

                //生成接口方法
                classCon.append("\t"+"/**\n\t* 分页查询符合条件的所有记录\n\t* @Param condition 实体接收参数\n\t* */\n" +"\t" +"List<"+beanName+"> getPagedValue(@Param(\"condition\")"+beanName+" record, @Param(\"page\")Pagination pagination);\n\n");
                classCon.append("\t"+"/**\n\t* 查询符合条件的所有记录\n\t* */\n" +"\t" +"List<"+beanName+"> selectAllValueByCondition(@Param(\"condition\")"+beanName+" record);\n\n");
                classCon.append("\t"+"/**\n\t* 根据主码数据获取对应的一条数据\n\t* */\n" +"\t" +beanName+" selectOneValue(@Param(\"condition\")"+beanName+" record);\n\n");
                if(existKey){
                    classCon.append("\t"+"/**\n\t* 通过Id(主键)查询一条记录\n\t* */\n" +"\t" +beanName+" selectOneValueBykey(@Param(\""+keyColumn+"\")String key);\n\n");
                }

                //拼接(Dao接口）文件内容
                StringBuffer content=new StringBuffer();
                content.append(packageCon);
                content.append(importCon.toString());
                content.append(daoHeadCon);
                content.append(className);
                content.append(classCon.toString());
                content.append("\n}");
                HtFileUtil.createFileAtPath(path+"/", fileName+".java", content.toString());
            }
            return true;
        }
        return false;
    }

}
