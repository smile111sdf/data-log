package com.guoquan.store.operation.log.config;

import com.alibaba.fastjson.JSONObject;
import com.guoquan.store.operation.log.mq.OpeUserOperatorProducer;
import com.guoquan.store.operation.log.utils.OpeSqlParserUtils;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @ClassName CenterSynchroService
 * @Description 异步发送MQ消息
 * @Date 2021/7/20 10:00
 * @Author wangLuLu
 * @Version 1.0
 */
@EnableAsync
@Slf4j
@Component
public class OpeCenterSynchroService {

    @Autowired
    private OpeUserOperatorProducer userOperatorProducer;

    @Value("${spring.application.name}")
    private String appName;

    private static final String DELIMITER = ">>>>";

    /**
     * 只记录用户新增-编辑的操作
     * @param sql 执行的sql语句
     * @param operatorInfo 用户信息+操作模块+设备IP
     * @param ms MappedStatement
     * @throws JSQLParserException
     */
    @Async
    public void userOperator(String sql, String operatorInfo, MappedStatement ms) throws JSQLParserException {
        //获取SQL类型 UPDATE INSERT等
        SqlCommandType sqlCommandType = ms.getSqlCommandType();
        Map<String, Object> map = null;
        if(sqlCommandType.equals(SqlCommandType.UPDATE)){
            //解析update sql 结构
            map = OpeSqlParserUtils.parserUpdateSql(sql);
            this.prepareSendMQMessage(sql, operatorInfo, ms, map);
        }else if(sqlCommandType.equals(SqlCommandType.INSERT)){
            //解析insert sql 结构
            map = OpeSqlParserUtils.parserInsertSql(sql);
            this.prepareSendMQMessage(sql, operatorInfo, ms, map);
        }
    }

    /**
     * 保存用户操作数据库记录
     * @param sql 执行的sql语句
     * @param operatorInfo 用户信息+操作模块+设备IP
     * @param ms MappedStatement
     * @param map 操作数据表明细
     * @return
     */
    private void prepareSendMQMessage(String sql, String operatorInfo, MappedStatement ms, Map<String, Object> map) {
        if(StringUtils.isBlank(operatorInfo)){
            return ;
        }
        JSONObject jsonObject = JSONObject.parseObject(operatorInfo);
        if (jsonObject == null) {
            jsonObject = new JSONObject();
        }
        jsonObject.put("appId",jsonObject.get("appId") + DELIMITER + appName);
        jsonObject.put("sql", sql);
        jsonObject.put("sqlCommandType", ms.getSqlCommandType());
        jsonObject.put("tableName",String.join(",", (List<String>) map.get("tableName")));
        jsonObject.put("column",String.join(",", (List<String>) map.get("column")));
        jsonObject.put("values",String.join(",", (List<String>) map.get("values")));
        jsonObject.put("threadId",jsonObject.get("requestId"));
        jsonObject.put("condition",map.get("condition"));
        userOperatorProducer.sendStoreGroupMessage(OpeConstants.USER_OPERATOR_TAG, JSONObject.toJSONString(jsonObject));
    }
}
