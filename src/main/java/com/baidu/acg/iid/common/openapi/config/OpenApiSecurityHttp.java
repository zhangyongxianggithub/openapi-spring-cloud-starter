package com.baidu.acg.iid.common.openapi.config;

import lombok.Data;

/**
 * @version 1.0
 * @name: zhangyongxiang
 * @author: zhangyongxiang@baidu.com
 * @date 2022/7/15 18:19
 * @description:
 **/
@Data
public class OpenApiSecurityHttp {
    
    private boolean enable = false;
    
    private String description;
    
    private String schema;
    
    private String format;
    
}
