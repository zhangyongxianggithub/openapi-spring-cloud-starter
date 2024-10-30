package com.baidu.acg.iid.common.openapi.config;

import java.util.Map;

import com.google.common.collect.Maps;

import lombok.Data;

/**
 * @version 1.0
 * @name: zhangyongxiang
 * @author: zhangyongxiang@baidu.com
 * @date 2022/7/15 18:15
 * @description:
 **/
@Data
public class OpenApiServer {
    
    private boolean useDefault = true;
    
    private String url;
    
    private Map<String, String> variables = Maps.newHashMap();
    
}
