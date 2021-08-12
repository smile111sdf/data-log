package com.guoquan.store.operation.log.config;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @Date 2021/7/22 22:21
 * @Author wangLuLu
 * @Version 1.0
 */

@Component
public class OpeAuthConfig {

    /**
     * token check
     */
    private final static String TOKEN_CHECK_URL = "token.check.url";

    private final static String AUTH_SWITCH = "auth.switch";

    private final static String DB_NAME = "store.db.name";

    private final static String OPERATOR_SWITCH = "user.operator.log.switch";

    @ApolloConfig
    private Config config;

    public String getTokenCheckUrl() {
        return config.getProperty(TOKEN_CHECK_URL, StringUtils.EMPTY);
    }

    public Boolean getAuthSwitch() {
        return config.getBooleanProperty(AUTH_SWITCH,false);
    }

    public String getDataBaseName(){
        return config.getProperty(DB_NAME,"dev_store");
    }

    public boolean getOperatorSwitch(){
        return config.getBooleanProperty(OPERATOR_SWITCH,false);
    }

}
