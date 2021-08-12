package com.guoquan.store.operation.log.interceptor;

import com.alibaba.fastjson.JSON;
import com.guoquan.store.operation.log.config.OpeAuthConfig;
import com.guoquan.store.operation.log.dto.OpeCheckTokenVO;
import com.guoquan.store.operation.log.utils.OpeUUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;


/**
 * @Description
 * @Date 2021/7/22 10:45 
 * @Author wangLuLu
 * @Version 1.0
 */

@Slf4j
@Component
public class OpeAuthInterceptor extends HandlerInterceptorAdapter {

    public static ThreadLocal<Map<String, Object>> userInfoThreadLocal = new ThreadLocal<>();

    @Autowired
    private OpeAuthConfig authConfig;

    @Value("${spring.application.name}")
    private String appName;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("guoquan-store-operation-log-com.guoquan.store.operation.log.interceptor-start.........");
        if (!authConfig.getOperatorSwitch()) {
            //判断是否开启记录用户操作的功能
            return true;
        }
        String authorization = request.getHeader("Authorization");
        if (StringUtils.isEmpty(authorization)) {
            String[] authorizations = request.getParameterValues("Authorization");
            if (authorizations != null && authorizations.length > 0) {
                authorization = authorizations[0];
            }
        }
        log.info("authorization={}", authorization);

        ResponseEntity<String> responseEntity = null;
        OpeCheckTokenVO checkTokenVO = null;
        String tokenCheckUrl = authConfig.getTokenCheckUrl();
        log.info("-----token拦截校验器:tokenCheckUrl={}", tokenCheckUrl);
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
            requestHeaders.add("Authorization", authorization);
            HttpEntity<String> requestEntity = new HttpEntity<String>(null, requestHeaders);
            responseEntity = restTemplate.exchange(tokenCheckUrl, HttpMethod.POST, requestEntity, String.class);
            checkTokenVO = JSON.parseObject(responseEntity.getBody(), OpeCheckTokenVO.class);
        } catch (RestClientException e) {
            log.error("sso.AuthInterceptor 调登录门户验证token出错 token={} 请求地址={}", request.getHeader("Authorization"), tokenCheckUrl);
        }
        getOperatorInfo(request, checkTokenVO);
        return true;
    }

    /**
     * 封装用户的操作信息
     * @param request
     * @param checkTokenVO
     */
    private void getOperatorInfo(HttpServletRequest request, OpeCheckTokenVO checkTokenVO) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userInfo", checkTokenVO.getData());
        userInfo.put("deviceIp", getIpAddress(request));
        try {
            String module = URLDecoder.decode(StringUtils.isNotBlank(request.getHeader("module")) ? request.getHeader("module") : "");
            String operatorType = URLDecoder.decode(StringUtils.isNotBlank(request.getHeader("operatorType")) ? request.getHeader("operatorType") : "");
            userInfo.put("module", module);
            userInfo.put("operatorType", operatorType);
        } catch (Exception e) {
            log.error("调用AESUtils.Decrypt()失败：{}", e);
        }
        userInfo.put("appId",appName);
        userInfo.put("requestId", OpeUUIDUtil.getUUID());
        userInfoThreadLocal.set(userInfo);
    }

    /**
     * 获取设备IP
     * @param request
     * @return
     */
    public String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
                //根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ipAddress = inet.getHostAddress();
            }
        }
        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ipAddress != null && ipAddress.length() > 15) { //"***.***.***.***".length() = 15
            if (ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        return ipAddress;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
    }
}
