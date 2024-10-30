package com.baidu.acg.iid.common.openapi.config;

import java.util.List;

import com.google.common.collect.Lists;

import lombok.Data;

/**
 * @version 1.0
 * @name: zhangyongxiang
 * @author: zhangyongxiang@baidu.com
 * @date 2022/7/15 18:12
 * @description:
 **/
@Data
public class OpenApiProperties {
    
    /**
     * profile激活配置
     */
    private List<String> inactiveProfiles = Lists.newArrayList();
    
    /**
     * openapi版本
     */
    private String openapiVersion = "3.0.1";
    
    /**
     * info信息
     */
    private OpenApiInfo info = new OpenApiInfo();
    
    /**
     * server信息
     */
    private OpenApiServer server = new OpenApiServer();
    
    /**
     * 认证信息
     */
    private OpenApiSecurity security = new OpenApiSecurity();
    
    /**
     * path过滤的信息
     */
    private OpenApiPath path = new OpenApiPath();
    
    /**
     * 是否定义自定义分组
     */
    private OpenApiGroup group = new OpenApiGroup();
    
}
