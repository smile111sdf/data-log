package com.guoquan.store.operation.log.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @Description Spring Bean
 * @Date 2021/7/22 10:36 
 * @Author wangLuLu
 * @Version 1.0
 */

@Component
public class OpeSpringBeansUtils implements ApplicationContextAware {
 
	private static ApplicationContext context;
 
	/**
	 * 根据bean的id来查找对象
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBeanById(String id){
		return (T) context.getBean(id);
	}
 
	/**
	 * 根据bean的name来查找对象
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBeanByName(String name) {
		return (T) context.getBean(name);
	}
 
	/**
	 * 根据bean的CLass类类型来查找对象
	 * @param requiredType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBeanByClass(Class<?> requiredType) {
		return (T) context.getBean(requiredType);
	}
 
 
	public static ApplicationContext getContext() {
		return context;
	}
 
	@Override
	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {
		context = ctx;
	}
}