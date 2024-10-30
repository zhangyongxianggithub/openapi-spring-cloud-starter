package com.baidu.acg.iid.common.openapi;

import java.util.NoSuchElementException;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;

import com.baidu.acg.iid.common.openapi.config.OpenApiProperties;
import com.baidu.acg.iid.common.openapi.factory.DefaultGroupedOpenApiFactory;
import com.baidu.acg.iid.common.openapi.factory.DefaultOpenApiFactory;

import io.swagger.v3.oas.models.OpenAPI;
import lombok.extern.slf4j.Slf4j;

/**
 * @version 1.0
 * @name: zhangyongxiang
 * @author: zhangyongxiang@baidu.com
 * @date 2022/7/15 17:06
 * @description:
 **/
@Slf4j
public class SpringDocBeanSupport
        implements ImportBeanDefinitionRegistrar, EnvironmentAware {
    
    public static final String DEFAULT_OPEN_API_BEAN_NAME = "openApi";
    
    private Environment environment;
    
    private final PackageResolver packageResolver = new AnnotationPackageResolver();
    
    @Override
    public void setEnvironment(@NonNull final Environment environment) {
        this.environment = environment;
    }
    
    @Override
    public void registerBeanDefinitions(
            @NonNull final AnnotationMetadata importingClassMetadata,
            @NonNull final BeanDefinitionRegistry registry,
            @NonNull final BeanNameGenerator importBeanNameGenerator) {
        if (Boolean.parseBoolean(environment
                .getProperty("springdoc.api-docs.enabled", "false"))) {
            log.info("found remote config, enable springdoc");
            final BindResult<OpenApiProperties> bindResult = Binder
                    .get(environment).bind("openapi", OpenApiProperties.class);
            OpenApiProperties openApiProperties = new OpenApiProperties();
            try {
                openApiProperties = bindResult.get();
            } catch (final NoSuchElementException e) {
                log.warn(
                        "there is no openapi properties in environment, use default value");
            }
            
            SpringDocConfigPropertiesPostProcessor
                    .addScanPackage(packageResolver
                            .resolvePackage(importingClassMetadata, registry)
                            .toArray(new String[0]));
            
            if (log.isDebugEnabled()) {
                log.debug("load open api properties: {}", openApiProperties);
            }
            registry.registerBeanDefinition(DEFAULT_OPEN_API_BEAN_NAME,
                    new RootBeanDefinition(OpenAPI.class,
                            new DefaultOpenApiFactory(environment,
                                    openApiProperties)::createOpenApi));
            new DefaultGroupedOpenApiFactory(openApiProperties)
                    .createGroupedOpenApi()
                    .forEach((groupName, groupedOpenApi) -> registry
                            .registerBeanDefinition(groupName,
                                    new RootBeanDefinition(GroupedOpenApi.class,
                                            () -> groupedOpenApi)));
            registry.registerBeanDefinition(
                    "springDocConfigPropertiesPostProcessor",
                    new RootBeanDefinition(
                            SpringDocConfigPropertiesPostProcessor.class));
        } else {
            log.info("remote config noty found, springdoc is disabled");
        }
    }
}
