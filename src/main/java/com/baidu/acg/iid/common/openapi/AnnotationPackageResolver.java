package com.baidu.acg.iid.common.openapi;

import java.util.Arrays;
import java.util.Set;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.type.AnnotationMetadata;

import com.google.common.primitives.Primitives;

import lombok.extern.slf4j.Slf4j;

import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.ArrayUtils.nullToEmpty;
import static org.apache.commons.lang3.StringUtils.substringBefore;
import static org.apache.commons.lang3.StringUtils.substringBeforeLast;
import static org.springdoc.core.Constants.DOT;

/**
 * @version 1.0
 * @name: zhangyongxiang
 * @author: zhangyongxiang@baidu.com
 * @date 2022/7/18 11:19
 * @description: root package to scan controller， to implementation in the
 *               future
 **/

@Slf4j
public class AnnotationPackageResolver implements PackageResolver {
    
    public static final String PACKAGE_TO_SCAN_ATTR_NAME = "packageToScan";
    
    public static final String DOLLAR = "$";
    
    @Override
    public Set<String> resolvePackage(
            final AnnotationMetadata importingClassMetadata,
            final BeanDefinitionRegistry registry) {
        final MergedAnnotation<EnableOpenApiConfig> annotation = importingClassMetadata
                .getAnnotations().get(EnableOpenApiConfig.class);
        final Class<?>[] classes = nullToEmpty(
                annotation.getClassArray(PACKAGE_TO_SCAN_ATTR_NAME));
        final Set<Class<?>> nonPrimitiveClasses = Arrays.stream(classes)
                .filter(c -> !Primitives.allPrimitiveTypes().contains(c))
                .collect(toSet());
        if (!nonPrimitiveClasses.isEmpty()) {
            return Arrays.stream(classes).map(Class::getName)
                    .map(className -> substringBefore(className, DOLLAR))
                    .map(className -> substringBeforeLast(className, DOT))
                    .collect(toSet());
        } else {
            log.warn(
                    "no class specified, use the package to which spring main application belongs");
            return singleton(substringBeforeLast(substringBefore(
                    importingClassMetadata.getClassName(), DOLLAR), DOT));
        }
    }
}
