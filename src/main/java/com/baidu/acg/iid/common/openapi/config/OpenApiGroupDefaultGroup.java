package com.baidu.acg.iid.common.openapi.config;

import lombok.Data;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * @version 1.0
 * @name: zhangyongxiang
 * @author: zhangyongxiang@baidu.com
 * @date 2022/7/15 18:24
 * @description:
 **/
@Data
public class OpenApiGroupDefaultGroup {
    
    private boolean enable = true;
    
    private String aliasName = "default";
    
    public String getAliasName() {
        return isNotBlank(aliasName) ? aliasName : "default";
    }
}
