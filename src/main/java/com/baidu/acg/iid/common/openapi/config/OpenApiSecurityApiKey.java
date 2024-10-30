package com.baidu.acg.iid.common.openapi.config;

import lombok.Data;

/**
 * @version 1.0
 * @name: zhangyongxiang
 * @author: zhangyongxiang@baidu.com
 * @date 2022/7/15 18:18
 * @description:
 **/
@Data
public class OpenApiSecurityApiKey {
    
    private boolean enable = false;
    
    private String name = "iidp_token";
    
    private String description;
    
}
