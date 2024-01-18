package com.g7.framework.cat;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author dreamyao
 * @title 开启Cat监控
 * @date 2019/3/5 11:42 AM
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Configuration
@Import(CatAutoConfiguration.CatBeanDefinitionRegistrar.class)
public @interface EnableCat {

    /**
     * 用于控制是否开启cat mybatis、http、dubbo等调用监控的开关
     * @return 是否开启
     */
    boolean enable() default true;
}
