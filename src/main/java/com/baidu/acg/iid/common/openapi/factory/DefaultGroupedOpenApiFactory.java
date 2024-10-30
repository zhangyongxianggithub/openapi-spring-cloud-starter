package com.baidu.acg.iid.common.openapi.factory;

import java.util.List;
import java.util.Map;

import org.springdoc.core.GroupedOpenApi;

import com.baidu.acg.iid.common.openapi.config.OpenApiGroup;
import com.baidu.acg.iid.common.openapi.config.OpenApiGroupNamedGroup;
import com.baidu.acg.iid.common.openapi.config.OpenApiProperties;
import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;

import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

/**
 * @version 1.0
 * @name: zhangyongxiang
 * @author: zhangyongxiang@baidu.com
 * @date 2022/7/15 20:53
 * @description:
 **/
@Slf4j
public class DefaultGroupedOpenApiFactory implements GroupOpenApiFactory {
    
    private final OpenApiGroup openApiGroup;
    
    public DefaultGroupedOpenApiFactory(
            final OpenApiProperties openApiProperties) {
        this.openApiGroup = openApiProperties.getGroup();
    }
    
    @Override
    public Map<String, GroupedOpenApi> createGroupedOpenApi() {
        
        if (openApiGroup.isGroupBy()) {
            final List<GroupedOpenApi> groupedOpenApis = emptyIfNull(
                    openApiGroup.getNamedGroup()).stream()
                            .filter(OpenApiGroupNamedGroup::isValid)
                            .map(namedGroup -> GroupedOpenApi.builder()
                                    .group(namedGroup.getName())
                                    .pathsToMatch(namedGroup.getPaths()
                                            .toArray(new String[0]))
                                    .build())
                            .collect(toList());
            if (isNotEmpty(groupedOpenApis)
                    && openApiGroup.getDefaultGroup().isEnable()) {
                groupedOpenApis.add(GroupedOpenApi.builder()
                        .group(openApiGroup.getDefaultGroup().getAliasName())
                        .pathsToMatch("/**")
                        .pathsToExclude(groupedOpenApis.stream()
                                .map(GroupedOpenApi::getPathsToMatch)
                                .flatMap(List::stream).distinct()
                                .toArray(String[]::new))
                        .build());
            }
            return Maps.uniqueIndex(groupedOpenApis, GroupedOpenApi::getGroup);
        }
        return emptyMap();
        
    }
}
