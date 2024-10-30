package com.baidu.acg.iid.common.openapi;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @version 1.0
 * @name: zhangyongxiang
 * @author: zhangyongxiang@baidu.com
 * @date 2022/7/15 16:03
 * @description:
 **/
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Configuration
@Import(SpringDocBeanSupport.class)
public @interface EnableOpenApiConfig {
    
    /**
     * some attributes to develop next version
     * 
     * @return application class
     */
    Class<?>[] packageToScan() default {};
    
}
