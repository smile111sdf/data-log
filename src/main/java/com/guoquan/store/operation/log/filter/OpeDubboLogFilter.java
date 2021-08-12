package com.guoquan.store.operation.log.filter;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import com.alibaba.fastjson.JSONObject;
import com.ctrip.framework.apollo.core.spi.Ordered;
import com.guoquan.store.operation.log.interceptor.OpeAuthInterceptor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @Description 定义filter
 * @Date 2021/7/28 10:48 
 * @Author wangLuLu
 * @Version 1.0
 */

@Activate(group = {Constants.CONSUMER, Constants.PROVIDER}, order = Ordered.LOWEST_PRECEDENCE)
@Slf4j
public class OpeDubboLogFilter implements Filter{

	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		Result rs = null;
		try {
			log.info("operation-log-OpeDubboLogFilter-invoke()-start");
			Map<String, Object> map = OpeAuthInterceptor.userInfoThreadLocal.get();
			if(map != null){
				RpcContext.getContext().setAttachment("operatorInfo", JSONObject.toJSONString(map));
			}
			rs = invoker.invoke(invocation);
		}catch (Exception e) {
			log.error("OpeDubboLogFilter-invoke() fail:{}", e);
		}
		return rs;
	}

}

