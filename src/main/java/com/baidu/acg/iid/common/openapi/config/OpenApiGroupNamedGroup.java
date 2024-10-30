package com.baidu.acg.iid.common.openapi.config;

import java.util.List;

import com.google.common.collect.Lists;

import lombok.Data;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * @version 1.0
 * @name: zhangyongxiang
 * @author: zhangyongxiang@baidu.com
 * @date 2022/7/15 18:23
 * @description:
 **/
@Data
public class OpenApiGroupNamedGroup {
    
    private String name;
    
    private List<String> paths = Lists.newArrayList();
    
    public boolean isValid() {
        return isNotBlank(name) && isNotEmpty(paths);
    }
}
