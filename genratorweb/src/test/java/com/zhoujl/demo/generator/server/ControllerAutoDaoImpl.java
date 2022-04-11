package com.zhoujl.demo.generator.server;



import com.zhoujl.demo.utils.HtConfigUtil;
import com.zhoujl.demo.utils.HtFileUtil;
import com.zhoujl.demo.utils.HtNameUtil;
import com.zhoujl.demo.utils.StringUtils;

import java.util.List;

/**

 */
public class ControllerAutoDaoImpl implements ControllerAutoDao {

    //从GetTablesDaoImpl中获得装有所有表结构的List
    GetTablesDao getTables = new GetTablesDaoImpl();
    List<TableStruct> list = getTables.getTablesStruct();

    @Override
    public boolean createController(String headValue, String excludeTableName) {
        //获得配置文件的参数
        //项目路径
        String projectPath = HtConfigUtil.projectPath;
        //是否生成 controller
        String controllerFlag = HtConfigUtil.controllerFlag;
        //controller的包名
        String controllerPackage = HtConfigUtil.controllerPackage;

        if("true".equals(controllerFlag) ){
            //将包名com.xxx.xxx形式，替换成com/xxx/xxx形成
            String controllerPath = controllerPackage.replace(".", "/");
            //controller 的路径
            String path = projectPath + "/src/" + controllerPath;
            //遍历装有所有表结构的List
            for (int i = 0; i < list.size(); i++) {
                TableStruct dbTableInfo = list.get(i);
                String dbTableName = dbTableInfo.getTableName();
                String tableComment = dbTableInfo.getTableComment();
                //文件名
                String fileName = HtNameUtil.fileName(dbTableName) + "Controller";
                String beanName = HtNameUtil.fileName(dbTableName);
                String propertyTableBean = HtNameUtil.propertyName(beanName);

                String autowiredService = HtNameUtil.fileName(dbTableName) + "Service";
                String autowiredServicePro = HtNameUtil.propertyName(autowiredService);

                String conditionBeanName = beanName+"Condition";

                if(StringUtils.isNotNullOrEmpty(excludeTableName) && (dbTableName.contains(excludeTableName) || dbTableName.equals(excludeTableName)) ){
                    continue;
                }
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
                        keyColumn = columnName;
                        existKey = true;
                    }
                }

                //(Dao接口）文件内容
                String packageCon = "package " + controllerPackage + ";\n\n";
                StringBuffer importCon = new StringBuffer();
                importCon.append("import com.htzc.financebusiness.entity."+beanName+";\n");
                importCon.append("import com.htzc.financebusiness.entity.condition."+conditionBeanName+";\n");
                importCon.append("import com.htzc.financebusiness.service."+autowiredService+";\n");
                importCon.append("import com.htzc.financebusiness.api.ResultApi;\n");
                importCon.append("import com.htzc.financebusiness.common.mapper.search.PagedList;\n");
                importCon.append("import com.htzc.financebusiness.common.mapper.search.PagedSearchRequest;\n");
                importCon.append("import com.htzc.financebusiness.utils.StringUtils;\n");
                importCon.append("import io.swagger.annotations.Api;\n");
                importCon.append("import io.swagger.annotations.ApiOperation;\n");
                importCon.append("import lombok.extern.slf4j.Slf4j;\n");
                importCon.append("import org.springframework.beans.factory.annotation.Autowired;\n");
                importCon.append("import org.springframework.transaction.annotation.Transactional;\n");
                importCon.append("import org.springframework.web.bind.annotation.*;\n");

                String daoHeadCon = "/**\n" +
                        " * " + tableComment + "\n" +headValue;
                String classHead = "@Slf4j\n@RestController\n@RequestMapping(value = \"/sys/"+propertyTableBean+"\")\n";
                classHead += ("@Api(tags = \""+tableComment+"—"+fileName+"\")\n");
                String className = "public class " + fileName + " {\n\n";
                StringBuffer classCon = new StringBuffer();

                classCon.append("\t@Autowired\n");
                classCon.append("\t" + autowiredService + " " + autowiredServicePro + ";\n\n");

                //分页*********
                classCon.append("\t@ApiOperation(value = \"分页查询信息\")\n");
                classCon.append("\t@Transactional(rollbackFor = Exception.class)\n");
                classCon.append("\t@PostMapping(\"/getPaged\")\n");
                classCon.append("\tpublic PagedList<"+beanName+"> getPaged(@RequestBody PagedSearchRequest<"+conditionBeanName+"> pagedSearchRequest){\n");
                classCon.append("\t\treturn "+autowiredServicePro+".getPagedValue(pagedSearchRequest);\n");
                classCon.append("\t}\n\n");

                //条件查询*********
                classCon.append("\t@ApiOperation(value = \"条件查询信息\")\n");
                classCon.append("\t@Transactional(rollbackFor = Exception.class)\n");
                classCon.append("\t@PostMapping(\"/getAllByCondition\")\n");
                classCon.append("\tpublic ResultApi getAllByCondition(@RequestBody "+conditionBeanName+" condition){\n");
                classCon.append("\t\treturn "+autowiredServicePro+".selectAllValueByCondition(condition);\n");
                classCon.append("\t}\n\n");

                if(existKey){
                    //主键查询*********
                    classCon.append("\t@ApiOperation(value = \"主键查询信息\")\n");
                    classCon.append("\t@Transactional(rollbackFor = Exception.class)\n");
                    classCon.append("\t@GetMapping(\"/getOneBykey\")\n");
                    classCon.append("\tpublic ResultApi selectOneValueBykey(String id){\n");
                    classCon.append("\t\treturn "+autowiredServicePro+".selectOneValueBykey(id);\n");
                    classCon.append("\t}\n\n");

                    //逻辑删除********
                    classCon.append("\t@ApiOperation(value = \"逻辑删除\")\n");
                    classCon.append("\t@Transactional(rollbackFor = Exception.class)\n");
                    classCon.append("\t@GetMapping(\"/deleteValueBykey\")\n");
                    classCon.append("\tpublic ResultApi deleteValueBykey(String id){\n");
                    classCon.append("\t\treturn " + autowiredServicePro+".deleteValueBykey(id);\n");
                    classCon.append("\t}\n\n");

                }

                //新增********
                classCon.append("\t@ApiOperation(value = \"新增记录\")\n");
                classCon.append("\t@Transactional(rollbackFor = Exception.class)\n");
                classCon.append("\t@PostMapping(\"/addValue\")\n");
                classCon.append("\tpublic ResultApi addValue(@RequestBody " + conditionBeanName + " addCondition){\n");
                classCon.append("\t\treturn " + autowiredServicePro + ".addValue(addCondition);\n");
                classCon.append("\t}\n\n");

                //更新********
                classCon.append("\t@ApiOperation(value = \"更新记录\")\n");
                classCon.append("\t@Transactional(rollbackFor = Exception.class)\n");
                classCon.append("\t@PostMapping(\"/updateValue\")\n");
                classCon.append("\tpublic ResultApi updateValue(@RequestBody " + conditionBeanName + " updateCondition){\n");
                classCon.append("\t\treturn " + autowiredServicePro + ".updateValue(updateCondition);\n");
                classCon.append("\t}\n\n");

                //拼接(Dao接口）文件内容
                StringBuffer content=new StringBuffer();
                content.append(packageCon);
                content.append(importCon.toString());
                content.append(daoHeadCon);
                content.append(classHead);
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
