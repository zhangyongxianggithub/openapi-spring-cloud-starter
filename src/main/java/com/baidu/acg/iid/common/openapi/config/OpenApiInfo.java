package com.baidu.acg.iid.common.openapi.config;

import lombok.Data;

/**
 * @version 1.0
 * @name: zhangyongxiang
 * @author: zhangyongxiang@baidu.com
 * @date 2022/7/15 18:13
 * @description:
 **/
@Data
public class OpenApiInfo {
    
    private String title;
    
    private String summary;
    
    private String description;
    
    private String version;
    
    private OpenApiContact contact = new OpenApiContact();
}
