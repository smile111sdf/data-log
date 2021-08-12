package com.guoquan.store.operation.log.interceptor;

import com.alibaba.dubbo.rpc.RpcContext;
import com.guoquan.store.operation.log.config.OpeAuthConfig;
import com.guoquan.store.operation.log.config.OpeCenterSynchroService;
import com.guoquan.store.operation.log.utils.OpeSpringBeansUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

/**
 * @Description 自定义mybatis拦截器
 * @Date 2021/7/19 17:56 
 * @Author wangLuLu
 * @Version 1.0
 */

@SuppressWarnings({"rawtypes", "unchecked"})
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
@Slf4j
@Component
public class OpeMyInterceptor implements Interceptor {

    @Autowired
    private OpeCenterSynchroService centerSynchroService;

    @Autowired
    private OpeAuthConfig authConfig;

    @Override
    public Object intercept(Invocation invocation) throws Throwable{
        Object result = null;
        try {
            //执行sql
            result = invocation.proceed();
            //业务sql执行成功后，处理用户操作记录
            this.handleOperatorRecord(invocation);
            return result;
        } catch (Exception e){
            log.error("OpeMyInterceptor interceptor fail:{}", e);
            throw new Exception("OpeMyInterceptor interceptor fail:" + e.getMessage());
        }
    }

    /**
     * 处理用户操作记录信息
     * @param invocation
     */
    private void handleOperatorRecord(Invocation invocation){
        if(authConfig == null){
            authConfig = OpeSpringBeansUtils.getBeanByClass(OpeAuthConfig.class);
        }
        //是否开启记录用户操作开关
        if(!authConfig.getOperatorSwitch()){
            return;
        }
        log.info("==================记录用户操作记录公共拦截器开始调用======================");
        try {
            //获取用户信息
            String operatorInfo = RpcContext.getContext().getAttachment("operatorInfo");
            //发送sql变更信息
            if(!StringUtils.isEmpty(operatorInfo) && operatorInfo != "null"){
                MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
                Object parameter = null;
                if (invocation.getArgs().length > 1) {
                    parameter = invocation.getArgs()[1];
                }
                BoundSql boundSql = mappedStatement.getBoundSql(parameter);
                Configuration configuration = mappedStatement.getConfiguration();
                //获取sql语句
                String sql = showSql(configuration, boundSql);

                if(centerSynchroService == null){
                    centerSynchroService = OpeSpringBeansUtils.getBeanByClass(OpeCenterSynchroService.class);
                }
                centerSynchroService.userOperator(sql,operatorInfo,mappedStatement);
            }
            log.info("==================记录用户操作记录公共拦截器调用结束======================");
        }catch (Exception e){
            log.error("OpeMyInterceptor-handleOperatorRecord fail :{}", e);
        }
    }


    /**
     * 获取执行的sql
     * @param configuration
     * @param boundSql
     * @return
     */
    public String showSql(Configuration configuration, BoundSql boundSql) {
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
        if (parameterMappings.size() > 0 && parameterObject != null) {
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sql = sql.replaceFirst("\\?", getParameterValue(parameterObject));

            } else {
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                for (ParameterMapping parameterMapping : parameterMappings) {
                    String propertyName = parameterMapping.getProperty();
                    if (metaObject.hasGetter(propertyName)) {
                        Object obj = metaObject.getValue(propertyName);
                        sql = sql.replaceFirst("\\?", getParameterValue(obj));
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        sql = sql.replaceFirst("\\?", getParameterValue(obj));
                    }
                }
            }
        }
        return sql;
    }

    /**
     * 获取参数数值
     * @param obj
     * @return
     */
    private String getParameterValue(Object obj) {
        String value = null;
        if (obj instanceof String) {
            value = "'" + obj.toString() + "'";
        } else if (obj instanceof Date) {
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
            value = "'" + formatter.format(obj) + "'";
        } else {
            if (obj != null) {
                value = obj.toString();
            } else {
                value = "";
            }
        }
        return value;
    }


    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {}
}
