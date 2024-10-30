package com.baidu.acg.iid.common.openapi;

import java.util.Set;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @version 1.0
 * @name: zhangyongxiang
 * @author: zhangyongxiang@baidu.com
 * @date 2022/7/18 11:18
 * @description:
 **/

public interface PackageResolver {
    
    Set<String> resolvePackage(final AnnotationMetadata importingClassMetadata,
            final BeanDefinitionRegistry registry);
}
