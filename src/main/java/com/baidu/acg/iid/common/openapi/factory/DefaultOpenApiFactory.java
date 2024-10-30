package com.baidu.acg.iid.common.openapi.factory;

import java.util.List;

import org.springframework.core.env.Environment;

import com.baidu.acg.iid.common.openapi.config.OpenApiProperties;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;

import static java.util.Objects.nonNull;

/**
 * @version 1.0
 * @name: zhangyongxiang
 * @author: zhangyongxiang@baidu.com
 * @date 2022/7/15 20:48
 * @description:
 **/

public class DefaultOpenApiFactory implements OpenApiFactory {
    
    private final OpenApiProperties openApiProperties;
    
    private final OpenApiInfoFactory openApiInfoFactory;
    
    private final OpenApiServerFactory openApiServerFactory;
    
    private final OpenApiSecurityFactory openApiSecurityFactory;
    
    private final Environment environment;
    
    public DefaultOpenApiFactory(final Environment environment,
            final OpenApiProperties openApiProperties) {
        this.environment = environment;
        this.openApiProperties = openApiProperties;
        this.openApiInfoFactory = new DefaultOpenApiInfoFactory(
                openApiProperties.getInfo(), this.environment);
        this.openApiServerFactory = new DefaultOpenApiServerFactory(
                openApiProperties.getServer());
        this.openApiSecurityFactory = new DefaultOpenApiSecurityFactory(
                openApiProperties.getSecurity());
    }
    
    @Override
    public OpenAPI createOpenApi() {
        final OpenAPI openAPI = new OpenAPI()
                .info(this.openApiInfoFactory.createInfo());
        if (nonNull(openApiServerFactory.createServer())) {
            openAPI.addServersItem(openApiServerFactory.createServer());
        }
        if (nonNull(openApiSecurityFactory.createSecurityScheme())) {
            final List<NamedSecurityScheme> namedSecuritySchemes = openApiSecurityFactory
                    .createSecurityScheme();
            final Components components = new Components();
            final SecurityRequirement securityRequirement = new SecurityRequirement();
            namedSecuritySchemes.forEach(namedSecurityScheme -> {
                components.addSecuritySchemes(
                        namedSecurityScheme.getSecuritySchemeName(),
                        namedSecurityScheme.getSecuritySchema());
                securityRequirement
                        .addList(namedSecurityScheme.getSecuritySchemeName());
            });
            openAPI.components(components).addSecurityItem(securityRequirement);
        }
        return openAPI;
    }
}
