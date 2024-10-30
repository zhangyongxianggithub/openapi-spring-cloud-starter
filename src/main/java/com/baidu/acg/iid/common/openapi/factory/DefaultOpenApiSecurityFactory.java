package com.baidu.acg.iid.common.openapi.factory;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.baidu.acg.iid.common.openapi.config.OpenApiSecurity;
import com.google.common.collect.ImmutableList;

import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.extern.slf4j.Slf4j;

import static io.swagger.v3.oas.models.security.SecurityScheme.In.COOKIE;
import static io.swagger.v3.oas.models.security.SecurityScheme.In.HEADER;
import static io.swagger.v3.oas.models.security.SecurityScheme.Type.APIKEY;
import static io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections4.ListUtils.emptyIfNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.equalsAnyIgnoreCase;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * @version 1.0
 * @name: zhangyongxiang
 * @author: zhangyongxiang@baidu.com
 * @date 2022/7/15 21:57
 * @description:
 **/
@Slf4j
public class DefaultOpenApiSecurityFactory implements OpenApiSecurityFactory {
    
    private static final String SECURITY_SCHEME_NAME = "auth";
    
    private final OpenApiSecurity openApiSecurity;
    
    public DefaultOpenApiSecurityFactory(
            final OpenApiSecurity openApiSecurity) {
        this.openApiSecurity = openApiSecurity;
    }
    
    @Override
    public List<NamedSecurityScheme> createSecurityScheme() {
        
        if (emptyIfNull(openApiSecurity.getApiKey()).stream()
                .anyMatch(apiKey -> isTrue(apiKey.isEnable()))
                || openApiSecurity.getHttp().isEnable()) {
            if (emptyIfNull(openApiSecurity.getApiKey()).stream()
                    .anyMatch(apiKey -> isTrue(apiKey.isEnable()))) {
                final AtomicInteger index = new AtomicInteger(0);
                return openApiSecurity.getApiKey().stream()
                        .filter(apiKey -> isTrue(apiKey.isEnable()))
                        .map(enabledApiKey -> NamedSecurityScheme.builder()
                                .securitySchemeName(SECURITY_SCHEME_NAME
                                        + index.getAndIncrement())
                                .securitySchema(new SecurityScheme()
                                        .name(enabledApiKey.getName())
                                        .description(
                                                enabledApiKey.getDescription())
                                        .in(COOKIE).type(APIKEY))
                                .build())
                        .collect(toList());
            }
            if (openApiSecurity.getHttp().isEnable()) {
                final SecurityScheme securityScheme = new SecurityScheme();
                if (supportAuthenticationType(
                        openApiSecurity.getHttp().getSchema())) {
                    securityScheme
                            .description(
                                    openApiSecurity.getHttp().getDescription())
                            .in(HEADER).type(HTTP)
                            .scheme(lowerCase(
                                    openApiSecurity.getHttp().getSchema()))
                            .bearerFormat(
                                    openApiSecurity.getHttp().getFormat());
                } else {
                    securityScheme
                            .description(
                                    openApiSecurity.getHttp().getDescription())
                            .in(HEADER).type(APIKEY).name(AUTHORIZATION);
                }
                
                return ImmutableList.of(NamedSecurityScheme.builder()
                        .securitySchemeName(SECURITY_SCHEME_NAME)
                        .securitySchema(securityScheme).build());
            }
        }
        return emptyList();
    }
    
    public boolean supportAuthenticationType(final String schema) {
        return equalsAnyIgnoreCase(schema, "bearer", "basic", "digest");
    }
}
