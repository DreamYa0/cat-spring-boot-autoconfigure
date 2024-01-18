package com.g7.framework.cat;

import com.g7.framework.cat.dubbo.DubboCat;
import com.g7.framework.cat.plugin.CatMybatisPlugin;
import com.g7.framework.cat.rest.CatRestInterceptor;
import com.g7.framework.cat.rest.CatServletFilter;
import com.dianping.cat.Cat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.CollectionUtils;

@Import(value = {CatAutoConfiguration.CatBeanDefinitionRegistrar.class,CatAutoConfiguration.CatBeanPostProcessor.class})
public class CatAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(CatAutoConfiguration.class);

    /**
     * @author dreamyao
     * @title Bean注册器
     * @date 2019/3/5 11:38 AM
     * @since 1.0.0
     */
    static class CatBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

        @Override
        public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {

            AnnotationAttributes attributes = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(EnableCat.class.getName()));
            if (Boolean.FALSE.equals(CollectionUtils.isEmpty(attributes))) {

                boolean isEnable = attributes.getBoolean("enable");

                CatEnableApolloConfig config = CatEnableApolloConfig.getInstance();

                if (isEnable && config.isCurrentCatState()) {

                    BeanRegistrationUtils.registerBeanDefinitionIfNotExists(registry, "catMybatisPlugin", CatMybatisPlugin.class);
                    BeanRegistrationUtils.registerBeanDefinitionIfNotExists(registry, "catRestInterceptor", CatRestInterceptor.class);
                    BeanRegistrationUtils.registerBeanDefinitionIfNotExists(registry, "filterRegistrationBean", FilterRegistrationBean.class);

                    Cat.enable();
                    DubboCat.enable();
                    logger.info("------------------------------------------ enable cat log ------------------------------------------");

                } else {

                    Cat.disable();
                    DubboCat.disable();
                    logger.info("------------------------------------------ disable cat log ------------------------------------------");
                }
            }
        }
    }

    /**
     * @author dreamyao
     * @title Bean后置处理器
     * @date 2019/3/5 11:38 AM
     * @since 1.0.0
     */
    static class CatBeanPostProcessor implements BeanPostProcessor {

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

            if (bean instanceof FilterRegistrationBean) {

                logger.info("------------------------------------------ customization FilterRegistrationBean start ------------------------------------------");
                FilterRegistrationBean registration = (FilterRegistrationBean) bean;
                CatServletFilter filter = new CatServletFilter();
                registration.setFilter(filter);
                registration.addUrlPatterns("/*");
                registration.setName("cat-filter");
                registration.setOrder(1);
                return registration;
            }

            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            return bean;
        }
    }
}
