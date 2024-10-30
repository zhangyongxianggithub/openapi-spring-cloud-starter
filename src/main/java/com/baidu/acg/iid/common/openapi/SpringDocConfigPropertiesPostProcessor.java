package com.baidu.acg.iid.common.openapi;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.springdoc.core.SpringDocConfigProperties;
import org.springframework.beans.factory.InitializingBean;

import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

/**
 * @version 1.0
 * @name: zhangyongxiang
 * @author: zhangyongxiang@baidu.com
 * @date 2022/7/20 17:57
 * @description:
 **/
@Slf4j
public class SpringDocConfigPropertiesPostProcessor
        implements InitializingBean {
    
    private static final List<String> PACKAGE_TO_SCAN = Lists.newArrayList();
    
    private final SpringDocConfigProperties springDocConfigProperties;
    
    public SpringDocConfigPropertiesPostProcessor(
            final SpringDocConfigProperties springDocConfigProperties) {
        this.springDocConfigProperties = springDocConfigProperties;
    }
    
    @Override
    public void afterPropertiesSet() {
        if (isNotEmpty(PACKAGE_TO_SCAN)) {
            this.springDocConfigProperties.setPackagesToScan(PACKAGE_TO_SCAN);
            log.info("open api scan packages: {}", PACKAGE_TO_SCAN);
        } else {
            log.warn("no package scan setting, all http endpoint will appear");
        }
    }
    
    public static void addScanPackage(final String... packageNames) {
        if (ArrayUtils.isNotEmpty(packageNames)) {
            PACKAGE_TO_SCAN.addAll(Arrays.asList(packageNames));
            log.info("open api add scan package: {}", packageNames);
        }
    }
}
