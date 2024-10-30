package com.baidu.acg.iid.common.openapi.factory;

import io.swagger.v3.oas.models.info.Info;

/**
 * @version 1.0
 * @name: zhangyongxiang
 * @author: zhangyongxiang@baidu.com
 * @date 2022/7/15 20:46
 * @description:
 **/
@FunctionalInterface
public interface OpenApiInfoFactory {
    
    Info createInfo();
    
}
