package com.zhoujl.demo.generator.server;



import com.zhoujl.demo.utils.HtConfigUtil;
import com.zhoujl.demo.utils.HtFileUtil;
import com.zhoujl.demo.utils.HtNameUtil;
import com.zhoujl.demo.utils.StringUtils;

import java.util.List;

/**

 */
public class ServiceAutoDaoImpl implements ServiceAutoDao {

    //从GetTablesDaoImpl中获得装有所有表结构的List
    GetTablesDao getTables = new GetTablesDaoImpl();
    List<TableStruct> list = getTables.getTablesStruct();

    //通过表名、字段名称、字段类型创建Service接口
    @Override
    public boolean createService(String headValue, String excludeTableName) {
        //获得配置文件的参数
        //项目路径
        String projectPath = HtConfigUtil.projectPath;
        //是否生成Service
        String serviceFalg = HtConfigUtil.serviceFlag;
        //Service接口的包名
        String servicePackage = HtConfigUtil.servicePackage;
        String serviceImplPackage = HtConfigUtil.serviceImplPackage;

        //Bean实体类的包名
        String beanPackage = HtConfigUtil.beanPackage;
        if("true".equals(serviceFalg) ){
            //将包名com.xxx.xxx形式，替换成com/xxx/xxx形成
            String servicePath = servicePackage.replace(".", "/");
            //Service接口的路径
            String path = projectPath + "/src/" + servicePath;

            //将包名com.xxx.xxx形式，替换成com/xxx/xxx形成
            String serviceImplPath = serviceImplPackage.replace(".", "/");
            //ServiceImpl接口的路径
            String pathImpl = projectPath + "/src/" + serviceImplPath;


            //遍历装有所有表结构的List
            for (int i = 0; i < list.size(); i++) {
                TableStruct dbTableInfo = list.get(i);
                String dbTableName = dbTableInfo.getTableName();
                //文件名
                String fileName= HtNameUtil.fileName(dbTableName)+"Service";
                String beanName = HtNameUtil.fileName(dbTableName);
                String conditionBeanName = beanName+"Condition";

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

                //(Service接口）文件内容
                String packageCon = "package " + servicePackage + ";\n\n";
                StringBuffer importCon = new StringBuffer();
                String className = "public interface " + fileName + " extends BaseService<"+beanName+">{\n\n";
                StringBuffer classCon = new StringBuffer();

                //生成导包内容
                importCon.append("import com.htzc.financebusiness.entity."+beanName+";\n");
                importCon.append("import com.htzc.financebusiness.entity.condition."+conditionBeanName+";\n");
                importCon.append("import com.htzc.financebusiness.api.ResultApi;\n");
                importCon.append("import com.htzc.financebusiness.common.mapper.search.Pagination;\n");
                importCon.append("import com.htzc.financebusiness.common.mapper.search.PagedList;\n");
                importCon.append("import com.htzc.financebusiness.common.mapper.search.PagedSearchRequest;\n");
                importCon.append("import java.util.List;\n\n");



                //生成接口方法
                classCon.append("\t"+"//分页查询\n" +         "\t" +"public PagedList<"+beanName+"> getPagedValue(PagedSearchRequest<"+conditionBeanName+"> pagedSearchRequest);\n\n");
                classCon.append("\t"+"//条件查询\n" +         "\t" +"public ResultApi selectAllValueByCondition("+conditionBeanName+" condition);\n\n");
                classCon.append("\t"+"//不定条件 查询一条记录\n" +         "\t" + "public ResultApi selectOneValue("+conditionBeanName+" condition);\n\n");
                if(existKey){
                    classCon.append("\t"+"//主键条件 查询一条记录\n" +         "\t" + "public ResultApi selectOneValueBykey(String key);\n\n");
                    classCon.append("\t"+"//主键条件 删除一条记录\n" + "\t" + "public ResultApi deleteValueBykey(String key);\n\n");
                }
                classCon.append("\t"+"//添加数据\n" +         "\t" + "public ResultApi addValue("+conditionBeanName+" condition);\n\n");
                classCon.append("\t"+"//更新数据\n" +         "\t" + "public ResultApi updateValue("+conditionBeanName+" condition);\n\n");

                //拼接(Service接口）文件内容
                StringBuffer content=new StringBuffer();
                content.append(packageCon);
                content.append(importCon.toString());
                content.append(className);
                content.append(classCon.toString());
                content.append("\n}");
                HtFileUtil.createFileAtPath(path+"/", fileName+".java", content.toString());



                //文件名
                String implFileName= HtNameUtil.fileName(dbTableName)+"ServiceImpl";
                //(Service接口）文件内容
                String implPackageCon = "package " + serviceImplPackage + ";\n\n";
                //生成导包内容
                StringBuffer implimportCon = new StringBuffer();
                implimportCon.append("import com.htzc.financebusiness.entity."+beanName+";\n");
                implimportCon.append("import com.htzc.financebusiness.entity.condition."+conditionBeanName+";\n");
                implimportCon.append("import com.htzc.financebusiness.api.ResultApi;\n");
                implimportCon.append("import com.htzc.financebusiness.common.mapper.search.Pagination;\n");
                implimportCon.append("import com.htzc.financebusiness.common.mapper.search.PagedList;\n");
                implimportCon.append("import com.htzc.financebusiness.common.mapper.search.PagedSearchRequest;\n");
                implimportCon.append("import com.htzc.financebusiness.service.*;\n");
                implimportCon.append("import com.htzc.financebusiness.dao.*;\n");
                implimportCon.append("import org.springframework.beans.factory.annotation.Autowired;\n");
                implimportCon.append("import lombok.extern.slf4j.Slf4j;\n");
                implimportCon.append("import com.htzc.financebusiness.utils.StringUtils;\n");
                implimportCon.append("import org.springframework.stereotype.Service;\n");
                implimportCon.append("import java.util.List;\n\n");


                String autoDao = HtNameUtil.fileName(dbTableName) + "Dao";
                String propertyDao = HtNameUtil.propertyName(autoDao);

                String implClassName = "@Slf4j\n@Service\n";
                implClassName += "public class " + implFileName + " extends BaseServiceImpl<"+beanName+"> implements "+fileName+"{\n\n";
                StringBuffer implClassCon = new StringBuffer();
                implClassCon.append("\t@Autowired\n");
                implClassCon.append("\t" + autoDao + " " +propertyDao +";\n\n");
                implClassCon.append("\t@Override\n");
                implClassCon.append("\tpublic PagedList<"+beanName+"> getPagedValue(PagedSearchRequest<"+conditionBeanName+"> pagedSearchRequest) {\n");
                implClassCon.append("\t\tPagination pagination = pagedSearchRequest.toPagination();\n");
                implClassCon.append("\t\tList<"+beanName+"> pagedValueList = "+propertyDao+".getPagedValue(pagedSearchRequest.getSerachBean(), pagination);\n");
                implClassCon.append("\t\treturn new PagedList<>(pagedValueList, pagination);\n");
                implClassCon.append("\t}\n\n");


                implClassCon.append("\t@Override\n");
                implClassCon.append("\tpublic ResultApi selectAllValueByCondition("+conditionBeanName+" condition) {\n");
                implClassCon.append("\t\tList<"+beanName+"> valueList = "+propertyDao+".selectAllValueByCondition(condition);\n");
                implClassCon.append("\t\treturn new ResultApi().setData(valueList);\n");
                implClassCon.append("\t}\n\n");

                implClassCon.append("\t@Override\n");
                implClassCon.append("\tpublic ResultApi selectOneValue("+conditionBeanName+" condition) {\n");
                implClassCon.append("\t\t" + beanName + " oneValue = " + propertyDao + ".selectOneValue(condition);\n");
                implClassCon.append("\t\treturn new ResultApi().setData(oneValue);\n");
                implClassCon.append("\t}\n\n");

                if(existKey){
                    implClassCon.append("\t@Override\n");
                    implClassCon.append("\tpublic ResultApi selectOneValueBykey(String key) {\n");
                    implClassCon.append("\t\tif (StringUtils.isNotNullOrEmpty(key)) {\n");
                    implClassCon.append("\t\t\t"+beanName+" findValue = "+propertyDao+".selectOneValueBykey(key);\n");
                    implClassCon.append("\t\t\treturn new ResultApi().setData(findValue);\n");
                    implClassCon.append("\t\t} else {\n");
                    implClassCon.append("\t\t\treturn new ResultApi().error(\"缺少关键参数！\");\n");
                    implClassCon.append("\t\t}\n");
                    implClassCon.append("\t}\n\n");

                    implClassCon.append("\t@Override\n");
                    implClassCon.append("\tpublic ResultApi deleteValueBykey(String key) {\n");
                    implClassCon.append("\t\treturn null;\n");
                    implClassCon.append("\t}\n\n");
                }


                implClassCon.append("\t@Override\n");
                implClassCon.append("\tpublic ResultApi addValue("+conditionBeanName+" condition) {\n");
                implClassCon.append("\t\treturn null;\n");
                implClassCon.append("\t}\n\n");

                implClassCon.append("\t@Override\n");
                implClassCon.append("\tpublic ResultApi updateValue("+conditionBeanName+" condition) {\n");
                implClassCon.append("\t\treturn null;\n");
                implClassCon.append("\t}\n\n");


                //拼接(ServiceImpl）文件内容
                StringBuffer contentImpl = new StringBuffer();
                contentImpl.append(implPackageCon);
                contentImpl.append(implimportCon.toString());
                contentImpl.append(implClassName);
                contentImpl.append(implClassCon.toString());
                contentImpl.append("\n}");
                HtFileUtil.createFileAtPath(pathImpl + "/", implFileName + ".java", contentImpl.toString());


            }
            return true;
        }
        return false;
    }
}
