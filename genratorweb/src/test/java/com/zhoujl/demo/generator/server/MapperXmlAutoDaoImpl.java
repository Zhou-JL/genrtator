package com.zhoujl.demo.generator.server;



import com.zhoujl.demo.utils.*;

import java.util.List;

/**
 */
public class MapperXmlAutoDaoImpl implements MapperXmlAutoDao {

    //从GetTablesDaoImpl中获得装有所有表结构的List
    GetTablesDao getTables = new GetTablesDaoImpl();
    List<TableStruct> list = getTables.getTablesStruct();

    //通过表名、字段名称、字段类型创建Mapper.xml
    @Override
    public boolean createMapperXml(String headValue, String excludeTableName) {
        //获得配置文件的参数
        //项目路径
        String projectPath = HtConfigUtil.projectPath;
        //是否生成Mapper.xml
        String mapperXmlFalg=HtConfigUtil.mapperXmlFlag;
        //Mapper.xml的包名
        String mapperXmlPackage=HtConfigUtil.mapperXmlPackage;
        //Bean实体类的包名
        String beanPackage= HtConfigUtil.beanPackage;
        //Dao接口的包名
        String daoPackage=HtConfigUtil.daoPackage;
        if("true".equals(mapperXmlFalg) ){
            //将包名com.xxx.xxx形式，替换成com/xxx/xxx形成
            String mapperXmlPath=mapperXmlPackage.replace(".", "/");
            //Mapper.xml的路径
            String path =projectPath+"/src/"+mapperXmlPath;
            //遍历装有所有表结构的List
            for (int i = 0; i < list.size(); i++) {
                //表名
                String tableName =list.get(i).getTableName();
                //文件名
                String fileName= HtNameUtil.fileName(tableName)+"Mapper";
                String beanName =HtNameUtil.fileName(tableName);
                String daoName =HtNameUtil.fileName(tableName)+"Dao";
                if(StringUtils.isNotNullOrEmpty(excludeTableName) && (tableName.contains(excludeTableName) || tableName.equals(excludeTableName)) ){
                    continue;
                }


                //获得每个表的所有列结构
                List<ColumnStruct> columns =list.get(i).getColumns();

                //(Mapper.xml）文件内容
                StringBuffer headCon = new StringBuffer();
                headCon.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
                headCon.append("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n");
                headCon.append("<mapper namespace=\""+daoPackage+"."+daoName+"\">\n");

                /**
                 * 实体类与数据库表字段映射
                 * */
                StringBuffer resultMapCon = new StringBuffer();
                String resultMapId = beanName + "Map";
                resultMapCon.append("\t"+"<resultMap id=\""+resultMapId+"\" type=\""+beanPackage+"."+beanName+"\">\n");

                /**
                 * 公共：表的所有列名
                 * */
                StringBuffer colunmsColCon = new StringBuffer();
                String colunms_sqlId = "colunms_sql";
                colunmsColCon.append("\t"+"<sql id=\""+colunms_sqlId+"\">\n");

                /**
                 * 公共：表的所有列名的使用前判断
                 * */
                StringBuffer conditionColCon = new StringBuffer();
                String condition_sqlId = "condition_sql";
                conditionColCon.append("\t<sql id=\""+condition_sqlId+"\">\n");
                conditionColCon.append("\t\t<if test=\"condition != null\">\n");

                String keyDBColumn = "";
                boolean existKey = false;
                String keyColumn = "";
                boolean existDelFlag = false;
                //遍历List，将字段名称和字段类型、属性名写进文件
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
                    if("del_flag".equals(dbColumnName)){
                        existDelFlag = true;
                    }


                    //字段类型
                    String type= HtDataTypeUtil.getType(columns.get(j).getDataType());
                    String jdbcType = HtJdbcTypeUtil.getJdbcType(type, columns.get(j).getDataType());
                    if(jdbcType=="INT"||"INT".equals(jdbcType)){
                        jdbcType="INTEGER";
                    }

                    if(j==0){
                        resultMapCon.append("\t\t"+"<id column=\""+dbColumnName+"\" property=\""+columnName+"\" jdbcType=\""+jdbcType+"\"/>\n");
                        colunmsColCon.append("\t\ta."+dbColumnName);
                    }else{
                        resultMapCon.append("\t\t"+"<result column=\""+dbColumnName+"\" property=\""+columnName+"\" jdbcType=\""+jdbcType+"\"/>\n");
                        colunmsColCon.append(",a."+dbColumnName);
                    }
                    if("Date".equals(jdbcType) || "TIMESTAMP".equals(jdbcType)){
                        conditionColCon.append("\t\t\t<if test=\"condition."+columnName+" != null\">\n");
                    }else{
                        conditionColCon.append("\t\t\t<if test=\"condition."+columnName+" != null and condition."+columnName+" != ''\">\n");
                    }

                    conditionColCon.append("\t\t\t\tand a."+dbColumnName+" = #{condition."+ columnName +"}\n");
                    conditionColCon.append("\t\t\t</if>\n");
                }
                resultMapCon.append("\t"+"</resultMap>\n\n");
                conditionColCon.append("\t\t"+"</if>\n");
                conditionColCon.append("\t"+"</sql>\n\n");
                colunmsColCon.append("\n\t"+"</sql>\n\n");


                //--4
                StringBuffer getPagedByParamsCon=new StringBuffer();
                getPagedByParamsCon.append("\t"+"<select id=\"getPagedValue\" resultMap=\""+resultMapId+"\">\n");
                getPagedByParamsCon.append("\t\t"+"select <include refid=\""+colunms_sqlId+"\"/> from "+tableName+" a\n");
                getPagedByParamsCon.append("\t\t"+"<where>\n");
                if(existDelFlag){
                    getPagedByParamsCon.append("\t\t\t"+"and a.del_flag = 0 \n");
                }
                getPagedByParamsCon.append("\t\t\t<include refid=\""+ condition_sqlId +"\" />\n");
                getPagedByParamsCon.append("\t\t"+"</where>\n");
                getPagedByParamsCon.append("\t"+"</select>\n\n");


                //--1
                StringBuffer selectByConditionInfoCon = new StringBuffer();
                selectByConditionInfoCon.append("\t"+"<select id=\"selectAllValueByCondition\"  resultMap=\""+resultMapId+"\">\n");
                selectByConditionInfoCon.append("\t\t"+"select <include refid=\""+colunms_sqlId+"\"/> from " + tableName +" a\n");
                selectByConditionInfoCon.append("\t\t"+"<where>\n");
                if(existDelFlag){
                    selectByConditionInfoCon.append("\t\t\t"+"and a.del_flag = 0\n");
                }
                selectByConditionInfoCon.append("\t\t\t<include refid=\""+ condition_sqlId +"\" />\n");
                selectByConditionInfoCon.append("\t\t"+"</where>\n");
                selectByConditionInfoCon.append("\t"+"</select>\n\n");

                //--2
                StringBuffer findConditionInfoCon = new StringBuffer();
                findConditionInfoCon.append("\t"+"<select id=\"selectOneValue\"  resultMap=\""+resultMapId+"\">\n");
                findConditionInfoCon.append("\t\t"+"select <include refid=\""+colunms_sqlId+"\"/> from "+tableName+" a\n");
                findConditionInfoCon.append("\t\t"+"<where>\n");
                if(existDelFlag){
                    findConditionInfoCon.append("\t\t\t"+"and a.del_flag = 0 \n");
                }
                findConditionInfoCon.append("\t\t\t<include refid=\""+ condition_sqlId +"\" />\n");
                findConditionInfoCon.append("\t\t"+"</where>\n");
                findConditionInfoCon.append("\t"+"</select>\n\n");

                //--3
                StringBuffer selectByIdCon = new StringBuffer();
                if(existKey){
                    selectByIdCon.append("\t"+"<select id=\"selectOneValueBykey\" resultMap=\""+resultMapId+"\">\n");
                    selectByIdCon.append("\t\t"+"select <include refid=\""+colunms_sqlId+"\"/> from "+tableName+" a\n");
                    selectByIdCon.append("\t\t"+"<where>\n");
                    selectByIdCon.append("\t\t\t"+"and a."+keyDBColumn+"=#{"+keyColumn+"}\n");
                    if(existDelFlag){
                        selectByIdCon.append("\t\t\t"+"and a.del_flag = 0 \n");
                    }
                    selectByIdCon.append("\t\t"+"</where>\n");
                    selectByIdCon.append("\t"+"</select>\n\n");
                }


                //拼接(Mapper.xml）文件内容
                StringBuffer content=new StringBuffer();
                content.append(headCon);
                content.append(resultMapCon);
                content.append(colunmsColCon);
                content.append(conditionColCon);

                content.append(getPagedByParamsCon);
                content.append(selectByConditionInfoCon);
                content.append(findConditionInfoCon);
                content.append(selectByIdCon);

                content.append("</mapper>");
                HtFileUtil.createFileAtPath(path+"/", fileName+".xml", content.toString());
            }
            return true;
        }
        return false;
    }

}
