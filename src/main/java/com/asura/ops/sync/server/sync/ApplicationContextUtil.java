package com.asura.ops.sync.server.sync;

import org.springframework.context.ApplicationContext;

/**
 * @Author: Mars
 * @Description: 应用上下文
 * @Date: create in 2022/3/25 15:25
 */
public class ApplicationContextUtil {

    private static ApplicationContext applicationContext;

    public static void setApplicationContextAware(ApplicationContext context) {
        applicationContext = context;
    }

    public static <T> T getObject(Class<T> cls) {
        Object object = null;
        return applicationContext.getBean(cls);
    }
}
