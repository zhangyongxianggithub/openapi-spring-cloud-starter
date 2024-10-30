package com.baidu.acg.iid.common.openapi;

import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.cloud.config.client.DiscoveryClientConfigServiceBootstrapConfiguration;
import org.springframework.cloud.config.client.RetryProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.interceptor.RetryInterceptorBuilder;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

import org.aspectj.lang.annotation.Aspect;

import static com.baidu.acg.iid.common.openapi.OpenApiConfigServicePropertySourceLocator.OPEN_API_CONFIG_SERVER_RETRY_INTERCEPTOR;

/**
 * @version 1.0
 * @name: zhangyongxiang
 * @author: zhangyongxiang@baidu.com
 * @date 2022/7/15 11:20
 * @description:
 **/
@Configuration(proxyBeanMethods = false)
public class OpenApiConfigServiceBootstrapConfiguration {
    
    @Bean
    @ConditionalOnMissingBean(OpenApiConfigServicePropertySourceLocator.class)
    @ConditionalOnProperty(value = "spring.cloud.config.enabled",
            matchIfMissing = true)
    @ConditionalOnBean({
            DiscoveryClientConfigServiceBootstrapConfiguration.class,
            ConfigClientProperties.class })
    public OpenApiConfigServicePropertySourceLocator openApiConfigServicePropertySource(
            final ConfigClientProperties properties) {
        final OpenApiConfigServicePropertySourceLocator locator = new OpenApiConfigServicePropertySourceLocator(
                properties);
        return locator;
    }
    
    @ConditionalOnProperty("spring.cloud.config.fail-fast")
    @ConditionalOnClass({ Retryable.class, Aspect.class,
            AopAutoConfiguration.class })
    @Configuration(proxyBeanMethods = false)
    @EnableRetry(proxyTargetClass = true)
    @Import(AopAutoConfiguration.class)
    @EnableConfigurationProperties(RetryProperties.class)
    protected static class RetryConfiguration {
        
        @Bean
        @ConditionalOnMissingBean(
                name = OPEN_API_CONFIG_SERVER_RETRY_INTERCEPTOR)
        public RetryOperationsInterceptor openApiConfigServerRetryInterceptor(
                final RetryProperties properties) {
            return RetryInterceptorBuilder.stateless()
                    .backOffOptions(properties.getInitialInterval(),
                            properties.getMultiplier(),
                            properties.getMaxInterval())
                    .maxAttempts(properties.getMaxAttempts()).build();
        }
        
    }
}
