package com.baidu.acg.iid.common.openapi.factory;

import io.swagger.v3.oas.models.servers.Server;

/**
 * @version 1.0
 * @name: zhangyongxiang
 * @author: zhangyongxiang@baidu.com
 * @date 2022/7/15 21:32
 * @description:
 **/
@FunctionalInterface
public interface OpenApiServerFactory {
    
    Server createServer();
    
}
