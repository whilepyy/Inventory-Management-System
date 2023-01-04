package com.example.dakudemo.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author chh
 * @date 2022/2/19 21:05
 */
@Component
public class ApplicationContextUtils implements ApplicationContextAware {
    private  static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
    //根据bean名字获取bean对象
    public static Object getBean(String beanName){
        return context.getBean(beanName);
    }
}
