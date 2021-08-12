package com.guoquan.store.operation.log.mq;

import com.guoquan.mq.producer.GuoquanMqGroupProducer;
import com.guoquan.store.operation.log.config.OpeConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @Description 用户操作记录 生产者（基于sql的）
 * @Date 2021/7/19 17:59
 * @Author wangLuLu
 * @Version 1.0
 */

@Slf4j
@Component
public class OpeUserOperatorProducer extends GuoquanMqGroupProducer {

    @Override
    public String getGroup() {
        return OpeConstants.USER_OPERATOR_TOPIC_GROUP;
    }

    public void sendStoreGroupMessage(String tag,String message){
        log.info("UserOperatorProducer-sendStoreGroupMessage-start,tag:{} , send message:{}", tag, message);
        try {
            if (StringUtils.isBlank(message)){
                log.error("UserOperatorProducer-sendStoreGroupMessage-发送消息失败,参数为空:{},tag:{}", message, tag);
                return ;
            }
            sendMessage(OpeConstants.USER_OPERATOR_TOPIC,tag,message);
            log.info("UserOperatorProducer-sendStoreGroupMessage-success");
        } catch (Exception e) {
            log.error("UserOperatorProducer-sendStoreGroupMessage-fail,tag={}，message={},errorMsg={}", tag, message, e);
        }
    }

}
