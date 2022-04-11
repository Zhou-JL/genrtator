package com.zhoujl.demo.generator;


import com.zhoujl.demo.generator.server.*;


public class generator {

    /**
     * 自动生成点一点
     * */
    public static void main(String[] args) {
/*        String createTime = DateUtil.formatDate(DateUtil.now(), "yyyy-MM-dd HH:mm:ss");
        Map<String, String> map = System.getenv();
        String computerName = map.get("COMPUTERNAME");// 获取计算机名*/
        String entityHeadCon =
                " * @Author: zjl\n" +
                " * @company: 北京汉唐智创科技有限公司\n" +
                " * @time: 2022-02-14 14:00:00\n" +
                " * @see: com.zhoujl.demo.rpcservice.entity\n" +
                " * @Version: 1.0\n" +
                " */\n";

        generateBean(entityHeadCon, "act");
        generateDao(entityHeadCon, "act");
        generateMapper(entityHeadCon, "act");
//        generateService(entityHeadCon, "act");
//        generateController(entityHeadCon, "act");
    }


    //1.生成Bean实体类
    public static void generateBean(String headValue, String excludeTableName) {
        BeanAutoDao beanAuto = new BeanAutoDaoImpl();
        if (beanAuto.createBean(headValue, excludeTableName)) {
            System.out.println("所有Bean实体类生成成功");
        } else {
            System.out.println("所有Bean实体类生成失败");
        }
    }

    //2.生成Dao接口
    public static void generateDao(String headValue, String excludeTableName) {
        DaoAutoDao daoAuto = new DaoAutoDaoImpl();
        if(daoAuto.createDao(headValue, excludeTableName)){
            System.out.println("所有Dao接口生成成功");
        }else{
            System.out.println("所有Dao接口生成失败");
        }
    }
//
    //3.生成Mapper.xml
    public static void generateMapper(String headValue, String excludeTableName) {
        MapperXmlAutoDao mapperXmlAuto=new MapperXmlAutoDaoImpl();
        if(mapperXmlAuto.createMapperXml(headValue, excludeTableName)){
            System.out.println("所有Mapper.xml生成成功");
        }else{
            System.out.println("所有Mapper.xml生成失败");
        }
    }

//    //4.生成Service接口
    public static void generateService(String headValue, String excludeTableName){
        ServiceAutoDao serviceAuto = new ServiceAutoDaoImpl();
        if(serviceAuto.createService(headValue, excludeTableName)){
            System.out.println("所有Service接口生成成功");
        }else{
            System.out.println("所有Service接口生成失败");
        }
    }

//    //5.生成Controller
    public static void generateController(String headValue, String excludeTableName){
        ControllerAutoDao controllerAutoDao = new ControllerAutoDaoImpl();
        if(controllerAutoDao.createController(headValue, excludeTableName)){
            System.out.println("所有 Controller 接口生成成功");
        }else{
            System.out.println("所有 Controller 接口生成失败");
        }
    }




















}
