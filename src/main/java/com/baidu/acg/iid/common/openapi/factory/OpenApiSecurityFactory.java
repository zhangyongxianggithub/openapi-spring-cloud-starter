package com.baidu.acg.iid.common.openapi.factory;

import java.util.List;

/**
 * @version 1.0
 * @name: zhangyongxiang
 * @author: zhangyongxiang@baidu.com
 * @date 2022/7/15 21:52
 * @description:
 **/
@FunctionalInterface
public interface OpenApiSecurityFactory {
    
    List<NamedSecurityScheme> createSecurityScheme();
}
