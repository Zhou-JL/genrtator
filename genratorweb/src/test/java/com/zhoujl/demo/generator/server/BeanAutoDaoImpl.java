package com.zhoujl.demo.generator.server;


import com.zhoujl.demo.utils.*;

import java.util.List;

/**
 */
public class BeanAutoDaoImpl implements BeanAutoDao {

    //从GetTablesDaoImpl中获得装有所有表结构的List
    GetTablesDao getTables = new GetTablesDaoImpl();
    List<TableStruct> list = getTables.getTablesStruct();


    @Override
    public boolean createBean(String headValue, String excludeTableName) {
        try{
            //获得配置文件的参数
            //项目路径
            String projectPath = HtConfigUtil.projectPath;
            //是否生成实体类
            String beanFalg = HtConfigUtil.beanFlag;
            //Bean实体类的包名
            String beanPackage = HtConfigUtil.beanPackage;
            String conditionBeanPackage = HtConfigUtil.conditionBeanPackage;

            //判断是否生成实体类
            if ("true".equals(beanFalg)) {
                //将包名com.xxx.xxx形式，替换成com/xxx/xxx形成
                String beanPath = beanPackage.replace(".", "/");
                String conditionBeanPath = conditionBeanPackage.replace(".", "/");

                //Bean实体类的路径
                String path = projectPath + "/src/" + beanPath;
                String conditionPath = projectPath + "/src/" + conditionBeanPath;

                //遍历装有所有表结构的List
                for (int i = 0; i < list.size(); i++) {
                    //文件名
                    String tableName = list.get(i).getTableName();
                    String tableComment = list.get(i).getTableComment();
                    String fileName= HtNameUtil.fileName(tableName);

                    if(StringUtils.isNotNullOrEmpty(excludeTableName) && (tableName.contains(excludeTableName) || tableName.equals(excludeTableName)) ){
                        continue;
                    }

                    //获得每个表的所有列结构
                    List<ColumnStruct> columns =list.get(i).getColumns();
                    //(实体类）文件内容
                    String packageCon = "package " + beanPackage + ";\n\n";
                    StringBuffer importCon = new StringBuffer();
                    String entityHeadCon = "/**\n" +
                            " * "+tableComment+"\n"+
                            headValue +
                            "@Entity\n@Data\n@NoArgsConstructor\n@AllArgsConstructor\n@Table(name = \""+tableName+"\")\n";
                    String className ="public class "+fileName+" implements Serializable {\n\n";
                    StringBuffer classCon =new StringBuffer();

                    /*StringBuffer gettersCon = new StringBuffer();
                    StringBuffer settersCon = new StringBuffer();*/

                    //===================condition开始=======================
                    String conditionfileName = fileName + "Condition";
                    StringBuffer conditionImportCon = new StringBuffer();

                    //(实体扩展类）文件内容
                    String conditionPackageCon = "package " + conditionBeanPackage + ";\n\n";
                    String conditionEntityHeadCon = "/**\n" +
                            " * "+tableComment+" 扩展类\n"+
                            headValue +
                            "@Entity\n@Data\n";
                    String conditionClassName ="public class "+conditionfileName+" extends "+ fileName +" {\n\n";




                    //遍历List，将字段名称和字段类型写进文件
                    for (int j = 0; j < columns.size(); j++) {
                        ColumnStruct currentColumn = columns.get(j);
                        //变量名（属性名）
                        String dbColumnName = currentColumn.getColumnName();
                        String columnName = HtNameUtil.columnName(dbColumnName);
                        //获得数据类型
                        String type = currentColumn.getDataType();
                        //字段是否非空
                        String isNullable = currentColumn.getIsNullable();
                        //字段主键
                        String columnKey = currentColumn.getColumnKey();
                        //字段注释
                        String columnComment = currentColumn.getColumnComment();

                        //将mysql数据类型转换为java数据类型
                        String dateType = HtDataTypeUtil.getType(type);
                        //有date类型的数据需导包
                        if(("Timestamp".equals(dateType) || "Date".equals(dateType)) && !importCon.toString().contains("import java.util.Date;")){
                            importCon.append("import java.util.Date;\n");
                        }
                        if("BigDecimal".equals(dateType) && !importCon.toString().contains("import java.math.BigDecimal;")){
                            importCon.append("import java.math.BigDecimal;\n");
                        }

                        boolean columnNullAble = false;
                        if("YES".equals(isNullable)){
                            columnNullAble = true;
                        }else{
                            columnNullAble = false;
                        }

                        if("PRI".equals(columnKey)){
                            classCon.append("\t@Id\n");
                        }else{
                            classCon.append("\t@ApiModelProperty(\""+columnComment+"\")\n");
                        }
                        //生成属性
                        classCon.append("\t@Column(name = \""+dbColumnName+"\", nullable = "+columnNullAble+")\n");
                        classCon.append("\t"+"private "+dateType+" "+columnName+";\n\n");
                    }

                    importCon.append("import io.swagger.annotations.ApiModelProperty;\n");
                    importCon.append("import java.io.Serializable;\n");
                    importCon.append("import lombok.AllArgsConstructor;\n");
                    importCon.append("import lombok.Data;\n");
                    importCon.append("import lombok.NoArgsConstructor;\n");
                    importCon.append("import javax.persistence.*;\n\n");



                    //拼接(实体类）文件内容
                    StringBuffer content = new StringBuffer();
                    content.append(packageCon);
                    content.append(importCon.toString());
                    content.append(entityHeadCon);
                    content.append(className);
                    content.append(classCon.toString());
                    /*content.append(gettersCon.toString());
                    content.append(settersCon.toString());*/
                    content.append("}");
                    HtFileUtil.createFileAtPath(path + "/", fileName + ".java", content.toString());


                    //拼接(实体类）文件内容
                    conditionImportCon.append("import com.htzc.financebusiness.entity."+ fileName+";\n");
                    conditionImportCon.append("import lombok.Data;\n");
                    conditionImportCon.append("import javax.persistence.*;\n\n");

                    StringBuffer conditionContent = new StringBuffer();
                    conditionContent.append(conditionPackageCon);
                    conditionContent.append(conditionImportCon.toString());
                    conditionContent.append(conditionEntityHeadCon);
                    conditionContent.append(conditionClassName);
                    conditionContent.append("}");
                    HtFileUtil.createFileAtPath(conditionPath + "/", conditionfileName + ".java", conditionContent.toString());

                }

                return true;
            }
            return false;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }





}
