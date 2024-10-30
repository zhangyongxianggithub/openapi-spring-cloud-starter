package com.baidu.acg.iid.common.openapi.config;

import java.util.List;

import com.google.common.collect.Lists;

import lombok.Data;

/**
 * @version 1.0
 * @name: zhangyongxiang
 * @author: zhangyongxiang@baidu.com
 * @date 2022/7/15 18:22
 * @description:
 **/
@Data
public class OpenApiGroup {
    
    private boolean groupBy = false;
    
    private List<OpenApiGroupNamedGroup> namedGroup = Lists.newArrayList();
    
    private OpenApiGroupDefaultGroup defaultGroup = new OpenApiGroupDefaultGroup();
}
