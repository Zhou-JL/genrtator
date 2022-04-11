package com.zhoujl.demo.commom.mapper.intercept;

import cn.hutool.core.util.ReflectUtil;
import com.zhoujl.demo.commom.mapper.SQLHelper;
import com.zhoujl.demo.commom.mapper.search.PagedSearchRequest;
import com.zhoujl.demo.commom.mapper.search.Pagination;
import com.zhoujl.demo.commom.mapper.search.SearchRequest;
import com.zhoujl.demo.utils.HtReflectUtils;
import com.zhoujl.demo.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.executor.statement.BaseStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.core.env.ConfigurablePropertyResolver;
import org.springframework.stereotype.Component;

import javax.xml.bind.PropertyException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 */
@Slf4j
@Component
@Intercepts({
        //表明要拦截的接口、方法以及对应的参数类型，新版参数类型有两个
        @Signature(type = StatementHandler.class,
                method = "prepare",
                args = {Connection.class,  Integer.class})
})
/**
 *分页拦截器，用于拦截需要进行分页查询的操作，然后对其进行分页处理。
 *利用拦截器实现Mybatis分页的一个思路就是拦截StatementHandler接口的prepare方法
 *  ，然后在拦截器方法中把Sql语句改成对应的分页查询Sql语句
 * */
public class HtzcPageInterceptor implements Interceptor {

    private static String dialect = "";
    private static String pageSqlId = "";
    private static String countExcludeSqlId = "";
    private static String searchSqlId = "";
    private static String searchExpressionId = "";
    private static Boolean enabled = false;
    private static ConfigurablePropertyResolver configurablePropertyResolver;


    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        /**
         * 拦截接口类型
         * */
        if (invocation.getTarget() instanceof RoutingStatementHandler) {
            RoutingStatementHandler statementHandler = (RoutingStatementHandler)invocation.getTarget();
            // 通过反射获取到当前RoutingStatementHandler对象的delegate属性
            BaseStatementHandler delegate = (BaseStatementHandler) ReflectUtil.getFieldValue(statementHandler, "delegate");
            //获取当前dao层执行的接口方法全名
            MappedStatement mappedStatement = (MappedStatement)ReflectUtil.getFieldValue(delegate, "mappedStatement");
            String sqlStatementId = mappedStatement.getId();
            // 获取到当前StatementHandler的
            // boundSql，这里不管是调用handler.getBoundSql()还是直接调用delegate.getBoundSql()结果是一样的，因为之前已经说过了
            // RoutingStatementHandler实现的所有StatementHandler接口方法里面都是调用的delegate对应的方法。
            BoundSql boundSql;
            // 拿到当前绑定Sql的参数对象，就是我们在调用对应的Mapper映射语句时所传入的参数对象
            Object parameterObject;
            String sql;
            /**
             * 分页sql判断入口
             * */
            if (sqlStatementId.matches(pageSqlId))
            {
                boundSql = delegate.getBoundSql();
                parameterObject = boundSql.getParameterObject();
                //获取查询参数中的分页 和 排序 参数
                Pagination searchRequest = this.getParameter(parameterObject);
                if (searchRequest != null )
                {
                    if (parameterObject == null) {
                        throw new NullPointerException("parameterObject尚未实例化！");
                    }
                    //获取当前sql中的数据库连接，未后续获取total数据的sql做准备
                    Connection connection = (Connection)invocation.getArgs()[0];
                    sql = boundSql.getSql();
                    String countSql;
                    if (this.enabled) {
                        countSql = SQLHelper.generatorCountSql(sql);
                    } else {
                        countSql = String.format("select count(0) from (%s) as tmp_count", sql);
                    }

                    //获取total总数的sql执行
                    PreparedStatement countStmt = connection.prepareStatement(countSql);
                    //重新创建要执行的sql，将当前配置信息、条件过滤参数名、条件过滤参数对应数据，分配给当前的BoundSql对象
                    BoundSql countBS = new BoundSql(mappedStatement.getConfiguration(), countSql, boundSql.getParameterMappings(), parameterObject);
                    MetaObject metaObject = mappedStatement.getConfiguration().newMetaObject(countBS);
                    MetaObject boundSqlObject = mappedStatement.getConfiguration().newMetaObject(boundSql);
                    metaObject.setValue("metaParameters", boundSqlObject.getValue("metaParameters"));
                    //参数赋值给当前 PreparedStatement
                    this.setParameters(countStmt, mappedStatement, boundSql, parameterObject);
                    //执行获取总数的sql
                    ResultSet rs = countStmt.executeQuery();
                    int count = 0;
                    if (rs.next()) {
                        count = rs.getInt(1);
                    }
                    rs.close();
                    countStmt.close();
                    //赋值保存当前总记录数，及计算对应总页数
                    searchRequest.setTotal(count);
                    String pageSql = this.generatePageSql(sql, searchRequest);
                    //重写当前boundSql中的sql语句 为 分页pageSql，后面执行该sql
                    HtReflectUtils.setValueByFieldName(boundSql, "sql", pageSql);
                    log.info("替换后分页后sql：", pageSql);
                }
            }else if(StringUtils.isNotNullOrEmpty(searchSqlId) && !sqlStatementId.matches(searchSqlId))
            //普通查询、新增、更新、删除
            {
                boundSql = delegate.getBoundSql();
                parameterObject = boundSql.getParameterObject();
                String orderClause = this.getOrderClause(parameterObject);
                if (orderClause != null && orderClause.length() > 0) {
                    if (parameterObject == null) {
                        throw new NullPointerException("parameterObject尚未实例化！");
                    }

                    sql = boundSql.getSql();
                    sql = this.generatePageSqlByOrder(sql, orderClause);
                    HtReflectUtils.setValueByFieldName(boundSql, "sql", sql);
                }
            }else if (StringUtils.isNotNullOrEmpty(searchExpressionId) && sqlStatementId.matches(searchExpressionId))
            //selectByExample
            {
                //后续补充
                /*boundSql = delegate.getBoundSql();
                parameterObject = boundSql.getParameterObject();
                SearchExpression<?> expression = this.getExpressionParameter(parameterObject);
                if (expression != null && expression.getPageSize() != null && expression.getPageSize() > 0) {
                    if (parameterObject == null) {
                        throw new NullPointerException("parameterObject尚未实例化！");
                    }

                    sql = boundSql.getSql();
                    sql = this.generateLimitSql(sql, expression);
                    HtReflectUtils.setValueByFieldName(boundSql, "sql", sql);
                 }*/
            }
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        //判断是否拦截这个类型对象（根据@Intercepts注解决定），然后决定是返回一个代理对象还是返回原对象。
        //故我们在实现plugin方法时，要判断一下目标类型，如果是插件要拦截的对象时才执行Plugin.wrap方法，否则的话，直接返回目标本身。
        log.info("拦截目标类型：{}", target.getClass().getName());
        if (target instanceof RoutingStatementHandler) {
            return Plugin.wrap(target, this);
        }else{
            return target;
        }
    }

    @Override
    public void setProperties(Properties properties) {
        log.info("拦截器需要一些变量对象，而且这个对象是支持可配置的");

        dialect = properties.getProperty("dialect");
        if (StringUtils.isNullOrEmpty(dialect)) {
            try {
                throw new PropertyException("dialect property is not found!");
            } catch (PropertyException var3) {
                var3.printStackTrace();
            }
        }
        pageSqlId = properties.getProperty("pageSqlId");
        enabled = StringUtils.isTrueString(properties.getProperty("countRegexEnabled"));
        searchSqlId = properties.getProperty("searchSqlId");
        searchExpressionId = properties.getProperty("searchExpressionId");


        log.debug("mybatis sql 匹配: pageSqlId{} searchSqlId{} searchExpressionId{}", pageSqlId, searchSqlId, searchExpressionId);
    }

    protected Pagination getParameter(Object parameterObject){
        if (parameterObject == null) {
            return null;
        }else{
            try{
                Pagination result = null;
                if (parameterObject instanceof PagedSearchRequest) {
                    PagedSearchRequest request = (PagedSearchRequest)parameterObject;
                    result = request.toPagination();
                } else if (parameterObject instanceof Pagination) {
                    result = (Pagination)parameterObject;
                } else if (parameterObject instanceof SearchRequest) {
                    result = new Pagination();
                    SearchRequest tmp = (SearchRequest)parameterObject;
                    result.setOrderBy(tmp.getOrderBy());
                }else if (parameterObject instanceof Map) {
                    boolean flag = false;
                    boolean orderFlag = false;
                    HashMap<String, Object> parameterObjectmap = (HashMap)parameterObject;
                    Iterator var6 = parameterObjectmap.entrySet().iterator();

                    while(var6.hasNext()) {
                        Map.Entry<String, Object> i = (Map.Entry)var6.next();
                        String key = (String)i.getKey();
                        Object item = i.getValue();
                        if (item != null) {
                            if (item instanceof Pagination) {
                                result = (Pagination)item;
                                flag = true;
                            }

                            if (!flag && item instanceof PagedSearchRequest) {
                                flag = true;
                                result = ((PagedSearchRequest)item).toPagination();
                            }

                            if (flag) {
                                break;
                            }
                        }
                    }
                }else {
                    Object value = HtReflectUtils.getValueByFieldName(parameterObject, "page");
                    if (value instanceof Pagination) {
                        result = (Pagination)value;
                    } /*else if (value instanceof Page) {
                        Page page = (Page)value;
                        Pagination pagination = new Pagination();
                        pagination.setOrderBy(page.getOrderBy());
                        pagination.setPageIndex(page.getPageNo());
                        pagination.setPageSize(page.getPageSize());
                        result = pagination;
                    }*/
                }
                return result;
            }catch(Exception e){
                log.error("sql语句处理操作异常 {} {}", e.getMessage(), e);
                return null;
            }
        }

    }

    private void setParameters(PreparedStatement ps, MappedStatement mappedStatement, BoundSql boundSql, Object parameterObject) throws SQLException {
        ErrorContext.instance().activity("setting parameters").object(mappedStatement.getParameterMap().getId());
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (parameterMappings != null) {
            Configuration configuration = mappedStatement.getConfiguration();
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            MetaObject metaObject = parameterObject == null ? null : configuration.newMetaObject(parameterObject);

            for(int i = 0; i < parameterMappings.size(); ++i) {
                ParameterMapping parameterMapping = (ParameterMapping)parameterMappings.get(i);
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    String propertyName = parameterMapping.getProperty();
                    PropertyTokenizer prop = new PropertyTokenizer(propertyName);
                    Object value;
                    if (parameterObject == null) {
                        value = null;
                    } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                        value = parameterObject;
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (propertyName.startsWith("__frch_") && boundSql.hasAdditionalParameter(prop.getName())) {
                        value = boundSql.getAdditionalParameter(prop.getName());
                        if (value != null) {
                            value = configuration.newMetaObject(value).getValue(propertyName.substring(prop.getName().length()));
                        }
                    } else {
                        value = metaObject == null ? null : metaObject.getValue(propertyName);
                    }

                    TypeHandler typeHandler = parameterMapping.getTypeHandler();
                    if (typeHandler == null) {
                        throw new ExecutorException("There was no TypeHandler found for parameter " + propertyName + " of statement " + mappedStatement.getId());
                    }

                    typeHandler.setParameter(ps, i + 1, value, parameterMapping.getJdbcType());
                }
            }
        }
    }

    private String generatePageSql(String sql, Pagination pagination) {
        if (pagination != null && StringUtils.isNotNullOrEmpty(dialect)) {
            StringBuilder pageSql = new StringBuilder();
            if ("mysql".equals(dialect)) {
                pageSql.append(sql);
                if (!sql.toLowerCase().contains("order by") && StringUtils.isNotNullOrEmpty(pagination.getOrderBy())) {
                    pageSql.append(" order by  ");
                    pageSql.append(camelToUnderline(pagination.getOrderBy()));
                }

                if (pagination.getPageSize() < 65535) {
                    if (pagination.getPageIndex() <= 1) {
                        pageSql.append(" limit ").append(pagination.getPageSize());
                    } else {
                        pageSql.append(" limit ").append((pagination.getPageIndex() - 1) * pagination.getPageSize()).append(",").append(pagination.getPageSize());
                    }
                }
            } else if ("oracle".equals(dialect)) {
                pageSql.append("getList * from (getList tmp_tb.*,ROWNUM row_id from (");
                pageSql.append(sql);
                pageSql.append(") as tmp_tb");
                pageSql.append("  where ROWNUM<=");
                pageSql.append(pagination.getPageIndex() * pagination.getPageSize());
                pageSql.append(") where row_id>");
                pageSql.append((pagination.getPageIndex() - 1) * pagination.getPageSize());
                if (!sql.toLowerCase().contains("order by") && StringUtils.isNotNullOrEmpty(pagination.getOrderBy())) {
                    pageSql.append(" order by  ");
                    pageSql.append(camelToUnderline(pagination.getOrderBy()));
                }
            }

            return pageSql.toString();
        } else {
            return sql;
        }
    }

    private static String camelToUnderline(String param) {
        if (param != null && !"".equals(param.trim())) {
            int len = param.length();
            StringBuilder sb = new StringBuilder(len);

            for(int i = 0; i < len; ++i) {
                char c = param.charAt(i);
                if (Character.isUpperCase(c)) {
                    sb.append('_');
                    sb.append(Character.toLowerCase(c));
                } else {
                    sb.append(c);
                }
            }

            return sb.toString();
        } else {
            return "";
        }
    }

    private String generatePageSqlByOrder(String sql, String orderClause) {
        return StringUtils.isNotNullOrEmpty(dialect) && StringUtils.isNotNullOrEmpty(orderClause) && !sql.toLowerCase().contains("order by") ? sql + " order by  " + camelToUnderline(orderClause) : sql;
    }

    protected String getOrderClause(Object parameterObject) {
        if (parameterObject == null) {
            return null;
        } else {
            try {
                if (parameterObject instanceof SearchRequest) {
                    SearchRequest tmp = (SearchRequest)parameterObject;
                    return tmp.getOrderBy();
                } else {
                    if (parameterObject instanceof Map) {
                        HashMap<String, Object> parameterObjectmap = (HashMap)parameterObject;
                        Iterator var3 = parameterObjectmap.entrySet().iterator();

                        while(var3.hasNext()) {
                            Map.Entry<String, Object> i = (Map.Entry)var3.next();
                            String key = (String)i.getKey();
                            Object item = i.getValue();
                            if (item != null) {
                                if (item instanceof String && "orderBy".equalsIgnoreCase(key)) {
                                    return (String)item;
                                }

                                if (item instanceof SearchRequest) {
                                    SearchRequest tmp = (SearchRequest)item;
                                    return tmp.getOrderBy();
                                }
                            }
                        }
                    } else {
                        Object orderBy = HtReflectUtils.getValueByFieldName(parameterObject, "orderBy");
                        if (orderBy instanceof String) {
                            return orderBy.toString();
                        }
                    }

                    return null;
                }
            } catch (Exception var8) {
                log.error(var8.getMessage(), var8);
                return null;
            }
        }
    }











}
