package com.baidu.acg.iid.common.openapi.factory;

import org.springframework.core.env.Environment;

import com.baidu.acg.iid.common.openapi.config.OpenApiInfo;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import lombok.extern.slf4j.Slf4j;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * @version 1.0
 * @name: zhangyongxiang
 * @author: zhangyongxiang@baidu.com
 * @date 2022/7/15 21:24
 * @description:
 **/
@Slf4j
public class DefaultOpenApiInfoFactory implements OpenApiInfoFactory {
    
    private final OpenApiInfo openApiInfo;
    
    private final Environment environment;
    
    public DefaultOpenApiInfoFactory(final OpenApiInfo openApiInfo,
            final Environment environment) {
        this.openApiInfo = openApiInfo;
        // TODO 应用spring.application.name
        this.environment = environment;
    }
    
    @Override
    public Info createInfo() {
        final Info info = new Info().title(openApiInfo.getTitle())
                .summary(openApiInfo.getSummary())
                .description(openApiInfo.getDescription())
                .version(openApiInfo.getVersion()).license(new License()
                        .name("Apache 2.0").url("https://ieip.bce.baidu.com"));
        if (isNotBlank(openApiInfo.getContact().getName())
                || isNotBlank(openApiInfo.getContact().getEmail())
                || isNotBlank(openApiInfo.getContact().getUrl())) {
            info.contact(
                    new Contact().email(openApiInfo.getContact().getEmail())
                            .name(openApiInfo.getContact().getName())
                            .url(openApiInfo.getContact().getUrl()));
        }
        return info;
    }
}
