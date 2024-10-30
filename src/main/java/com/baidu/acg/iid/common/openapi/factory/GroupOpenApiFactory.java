package com.baidu.acg.iid.common.openapi.factory;

import java.util.Map;

import org.springdoc.core.GroupedOpenApi;

/**
 * @version 1.0
 * @name: zhangyongxiang
 * @author: zhangyongxiang@baidu.com
 * @date 2022/7/15 20:52
 * @description:
 **/

public interface GroupOpenApiFactory {
    
    Map<String, GroupedOpenApi> createGroupedOpenApi();
}
