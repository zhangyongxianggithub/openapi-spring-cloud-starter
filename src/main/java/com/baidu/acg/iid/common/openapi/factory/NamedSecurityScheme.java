package com.baidu.acg.iid.common.openapi.factory;

import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.Builder;
import lombok.Getter;

/**
 * @version 1.0
 * @name: zhangyongxiang
 * @author: zhangyongxiang@baidu.com
 * @date 2022/7/15 21:54
 * @description:
 **/
@Getter
@Builder
public class NamedSecurityScheme {
    
    private String securitySchemeName;
    
    private SecurityScheme securitySchema;
}
