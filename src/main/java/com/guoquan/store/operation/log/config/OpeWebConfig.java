package com.guoquan.store.operation.log.config;

import com.guoquan.store.operation.log.interceptor.OpeAuthInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Description 基于OpeWebConfig配置拦截器
 * @Date 2021/7/28 14:25 
 * @Author wangLuLu
 * @Version 1.0
 */

@Configuration
public class OpeWebConfig implements WebMvcConfigurer {

    /**
     * 增加拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(OpeAuthorityInterceptor()).addPathPatterns("/**");
    }

    /**
     * 放行swagger
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
    }

    /**
     * 获取用户信息拦截器
     * @return
     */
    @Bean
    public OpeAuthInterceptor OpeAuthorityInterceptor() {
        return new OpeAuthInterceptor();
    }
}
