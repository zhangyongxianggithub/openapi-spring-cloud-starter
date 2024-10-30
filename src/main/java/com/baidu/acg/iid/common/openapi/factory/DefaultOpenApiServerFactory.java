package com.baidu.acg.iid.common.openapi.factory;

import com.baidu.acg.iid.common.openapi.config.OpenApiServer;

import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.servers.ServerVariable;
import io.swagger.v3.oas.models.servers.ServerVariables;
import lombok.extern.slf4j.Slf4j;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * @version 1.0
 * @name: zhangyongxiang
 * @author: zhangyongxiang@baidu.com
 * @date 2022/7/15 21:33
 * @description:
 **/
@Slf4j
public class DefaultOpenApiServerFactory implements OpenApiServerFactory {
    
    private final OpenApiServer openApiServer;
    
    public DefaultOpenApiServerFactory(final OpenApiServer openApiServer) {
        this.openApiServer = openApiServer;
    }
    
    @Override
    public Server createServer() {
        if (!openApiServer.isUseDefault()
                && isNotBlank(openApiServer.getUrl())) {
            final Server server = new Server().url(openApiServer.getUrl());
            final ServerVariables serverVariables = new ServerVariables();
            openApiServer.getVariables()
                    .forEach((variable, value) -> serverVariables
                            .addServerVariable(variable,
                                    new ServerVariable()._default(value)));
            server.variables(serverVariables);
            return server;
        }
        return null;
    }
}
