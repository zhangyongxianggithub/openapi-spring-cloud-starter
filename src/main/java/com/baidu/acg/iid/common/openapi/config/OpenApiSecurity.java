package com.baidu.acg.iid.common.openapi.config;

import java.util.List;

import com.google.common.collect.Lists;

import lombok.Data;

/**
 * @version 1.0
 * @name: zhangyongxiang
 * @author: zhangyongxiang@baidu.com
 * @date 2022/7/15 18:18
 * @description:
 **/
@Data
public class OpenApiSecurity {
    
    private List<OpenApiSecurityApiKey> apiKey = Lists.newArrayList();
    
    private OpenApiSecurityHttp http = new OpenApiSecurityHttp();
    
}
