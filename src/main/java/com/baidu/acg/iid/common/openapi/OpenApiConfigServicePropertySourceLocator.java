package com.baidu.acg.iid.common.openapi;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.boot.origin.Origin;
import org.springframework.boot.origin.OriginTrackedValue;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.cloud.bootstrap.support.OriginTrackedCompositePropertySource;
import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.cloud.config.client.ConfigClientStateHolder;
import org.springframework.cloud.config.client.DiscoveryClientConfigServiceBootstrapConfiguration;
import org.springframework.cloud.config.client.RetryProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.lang.NonNull;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.interceptor.RetryInterceptorBuilder;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;
import org.springframework.util.Assert;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.google.common.collect.Lists;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.springframework.cloud.config.client.ConfigClientProperties.AUTHORIZATION;
import static org.springframework.cloud.config.client.ConfigClientProperties.STATE_HEADER;
import static org.springframework.cloud.config.client.ConfigClientProperties.TOKEN_HEADER;
import static org.springframework.cloud.config.environment.EnvironmentMediaType.V2_JSON;
import static org.springframework.http.HttpHeaders.EMPTY;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.parseMediaType;
import static org.springframework.util.StringUtils.commaDelimitedListToStringArray;

/**
 * @version 1.0
 * @name: zhangyongxiang
 * @author: zhangyongxiang@baidu.com
 * @date 2022/7/15 11:27
 * @description:
 **/
@Slf4j
@Configuration
@SuppressWarnings("unchecked")
@ConditionalOnMissingBean(OpenApiConfigServicePropertySourceLocator.class)
@ConditionalOnProperty(value = "spring.cloud.config.enabled",
        matchIfMissing = true)
@ConditionalOnBean({ ConfigClientProperties.class,
        DiscoveryClientConfigServiceBootstrapConfiguration.class })
public class OpenApiConfigServicePropertySourceLocator
        implements PropertySourceLocator {
    
    public static final String OPEN_API_CONFIG_SERVER_RETRY_INTERCEPTOR = "openApiConfigServerRetryInterceptor";
    
    public static final String OPEN_API_PROPERTY_SOURCE_NAME = "openApiConfigService";
    
    public static final String PATH_WITH_VARIABLE_PREFIX = "/{name}/{profile}";
    
    public static final String OPENAPI_NAME_SUFFIX = "-openapi";
    
    public static final String SLASH = "/";
    
    public static final String OPENAPI_CONFIG_CLIENT_STATE = "openapi.config.client.state";
    
    public static final String OPENAPI_CONFIG_CLIENT_VERSION = "openapi.config.client.version";
    
    public static final String DEFAULT_OPEN_API_CONFIG_CLIENT_PROPERTY_SOURCE_NAME = "openApiConfigClient";
    
    @Setter
    private RestTemplate restTemplate;
    
    private final ConfigClientProperties defaultProperties;
    
    public OpenApiConfigServicePropertySourceLocator(
            final ConfigClientProperties defaultProperties) {
        this.defaultProperties = defaultProperties;
    }
    
    @Override
    @Retryable(interceptor = OPEN_API_CONFIG_SERVER_RETRY_INTERCEPTOR)
    public PropertySource<?> locate(final Environment environment) {
        final ConfigClientProperties properties = this.defaultProperties
                .override(environment);
        final CompositePropertySource composite = new OriginTrackedCompositePropertySource(
                OPEN_API_PROPERTY_SOURCE_NAME);
        final RestTemplate restClient = (this.restTemplate == null
                ? getSecureRestTemplate(properties)
                : this.restTemplate);
        Exception error = null;
        String errorBody = null;
        try {
            String[] labels = new String[] {};
            if (StringUtils.hasText(properties.getLabel())) {
                labels = commaDelimitedListToStringArray(properties.getLabel());
            }
            final String firstLabel = labels[0];
            final org.springframework.cloud.config.environment.Environment result = getRemoteEnvironment(
                    restClient, properties, firstLabel.trim());
            
            if (nonNull(result) && CollectionUtils
                    .isNotEmpty(result.getPropertySources())) {
                excludeNonOpenApiPropertySource(result,
                        properties.getProfile());
                log(result);
                result.getPropertySources().stream()
                        .map(source -> new OriginTrackedMapPropertySource(
                                source.getName(),
                                translateOrigins(source.getName(),
                                        (Map<String, Object>) source
                                                .getSource())))
                        .forEach(composite::addPropertySource);
                
                final HashMap<String, Object> map = new HashMap<>();
                putValue(map, OPENAPI_CONFIG_CLIENT_STATE, result.getState());
                putValue(map, OPENAPI_CONFIG_CLIENT_VERSION, defaultIfNull(
                        result.getVersion(), UUID.randomUUID().toString()));
                if (isNotEmpty(result.getPropertySources())) {
                    putValue(map, "springdoc.swagger-ui.enabled", "true");
                    putValue(map, "springdoc.api-docs.enabled", "true");
                } else {
                    putValue(map, "springdoc.swagger-ui.enabled", "false");
                    putValue(map, "springdoc.api-docs.enabled", "false");
                }
                
                composite.addFirstPropertySource(new MapPropertySource(
                        DEFAULT_OPEN_API_CONFIG_CLIENT_PROPERTY_SOURCE_NAME,
                        map));
            }
            if (!composite.getPropertySources().isEmpty()) {
                return composite;
            }
            errorBody = String.format("None of label %s found", firstLabel);
        } catch (final HttpServerErrorException e) {
            error = e;
            if (APPLICATION_JSON.includes(Optional.of(e)
                    .map(HttpServerErrorException::getResponseHeaders)
                    .orElse(EMPTY).getContentType())) {
                errorBody = e.getResponseBodyAsString();
            }
        } catch (final Exception e) {
            error = e;
        }
        if (properties.isFailFast()) {
            throw new IllegalStateException(
                    "Could not locate PropertySource and the fail fast property is set, failing"
                            + (errorBody == null ? "" : ": " + errorBody),
                    error);
        }
        log.warn("Could not locate PropertySource: "
                + (error != null ? error.getMessage() : errorBody));
        return null;
        
    }
    
    private void excludeNonOpenApiPropertySource(
            final org.springframework.cloud.config.environment.Environment environment,
            final String profile) {
        emptyIfNull(environment.getPropertySources())
                .removeIf(propertySource -> propertySource.getName()
                        .endsWith("application-" + profile + ".yml"));
    }
    
    @Override
    @Retryable(interceptor = OPEN_API_CONFIG_SERVER_RETRY_INTERCEPTOR)
    public Collection<PropertySource<?>> locateCollection(
            final org.springframework.core.env.Environment environment) {
        return PropertySourceLocator.locateCollection(this, environment);
    }
    
    private void log(
            final org.springframework.cloud.config.environment.Environment result) {
        log.info(
                "Located environment: name={}, profiles={}, label={}, version={}, state={}",
                result.getName(), result.getProfiles(), result.getLabel(),
                result.getVersion(), result.getState());
        if (log.isDebugEnabled()) {
            log.debug(
                    "Environment {} has {} property sources with {} properties.",
                    result.getName(),
                    emptyIfNull(result.getPropertySources()).size(),
                    emptyIfNull(result.getPropertySources()).stream().map(
                            org.springframework.cloud.config.environment.PropertySource::getSource)
                            .map(Map::size).mapToInt(Integer::intValue).sum());
            
        }
    }
    
    private Map<String, Object> translateOrigins(final String name,
            final Map<String, Object> source) {
        final Map<String, Object> withOrigins = new HashMap<>();
        for (final Map.Entry<String, Object> entry : source.entrySet()) {
            boolean hasOrigin = false;
            
            if (entry.getValue() instanceof Map) {
                @SuppressWarnings("unchecked")
                final Map<String, Object> value = (Map<String, Object>) entry
                        .getValue();
                if (value.size() == 2 && value.containsKey("origin")
                        && value.containsKey("value")) {
                    final Origin origin = new OpenApiConfigServiceOrigin(name,
                            value.get("origin"));
                    final OriginTrackedValue trackedValue = OriginTrackedValue
                            .of(value.get("value"), origin);
                    withOrigins.put(entry.getKey(), trackedValue);
                    hasOrigin = true;
                }
            }
            
            if (!hasOrigin) {
                withOrigins.put(entry.getKey(), entry.getValue());
            }
        }
        return withOrigins;
    }
    
    private void putValue(final HashMap<String, Object> map, final String key,
            final String value) {
        if (StringUtils.hasText(value)) {
            map.put(key, value);
        }
    }
    
    private org.springframework.cloud.config.environment.Environment getRemoteEnvironment(
            final RestTemplate restTemplate,
            final ConfigClientProperties properties, final String label) {
        final String path = getRequestPath(label);
        final int noOfUrls = properties.getUri().length;
        if (noOfUrls > 1) {
            log.info("Multiple Config Server Urls found listed, {}",
                    (Object) properties.getUri());
        }
        final Object[] args = resolveArgs(properties, label);
        for (int index = 0; index < noOfUrls; index++) {
            
            log.info("Fetching {} open api config from server at : {}",
                    properties.getName(),
                    properties.getCredentials(index).getUri());
            
            try {
                final HttpHeaders headers = getHttpHeaders(properties, index);
                final HttpEntity<Void> entity = new HttpEntity<>(null, headers);
                final ResponseEntity<org.springframework.cloud.config.environment.Environment> response = restTemplate
                        .exchange(
                                properties.getCredentials(index).getUri()
                                        + path,
                                GET, entity,
                                org.springframework.cloud.config.environment.Environment.class,
                                args);
                if (response.getStatusCode() == OK) {
                    return response.getBody();
                }
            } catch (final HttpClientErrorException e) {
                if (e.getStatusCode() != NOT_FOUND) {
                    throw e;
                }
            } catch (final ResourceAccessException e) {
                log.info(
                        "Connect Timeout Exception on Url {}. Will be trying the next url if available",
                        properties.getCredentials(index).getUri());
                if (index >= noOfUrls - 1) {
                    throw e;
                }
            }
        }
        
        return null;
    }
    
    private HttpHeaders getHttpHeaders(final ConfigClientProperties properties,
            final int index) {
        final ConfigClientProperties.Credentials credentials = properties
                .getCredentials(index);
        final String username = credentials.getUsername();
        final String password = credentials.getPassword();
        final HttpHeaders headers = new HttpHeaders();
        headers.setAccept(singletonList(parseMediaType(V2_JSON)));
        addAuthorizationToken(properties, headers, username, password);
        if (StringUtils.hasText(properties.getToken())) {
            headers.add(TOKEN_HEADER, properties.getToken());
        }
        if (StringUtils.hasText(ConfigClientStateHolder.getState())
                && properties.isSendState()) {
            headers.add(STATE_HEADER, ConfigClientStateHolder.getState());
        }
        return headers;
    }
    
    private String getName(final ConfigClientProperties properties) {
        return properties.getName() + OPENAPI_NAME_SUFFIX;
    }
    
    private String getRequestPath(final String label) {
        String path = PATH_WITH_VARIABLE_PREFIX;
        if (StringUtils.hasText(label)) {
            path = path + "/{label}";
        }
        return path;
    }
    
    private Object[] resolveArgs(final ConfigClientProperties properties,
            final String label) {
        final List<String> args = Lists.newArrayListWithCapacity(3);
        args.add(getName(properties));
        args.add(properties.getProfile());
        if (StringUtils.hasText(label)) {
            args.add(label.replace(SLASH, "(_)"));
        }
        return args.toArray();
    }
    
    private RestTemplate getSecureRestTemplate(
            final ConfigClientProperties client) {
        final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory
                .setReadTimeout(Optional.of(client.getRequestReadTimeout())
                        .filter(readTimeout -> readTimeout > 0)
                        .orElse((60 * 1000 * 3) + 5000));
        requestFactory.setConnectTimeout(
                Optional.of(client.getRequestConnectTimeout())
                        .filter(connectTimeout -> connectTimeout > 0)
                        .orElse(1000 * 10));
        final RestTemplate template = new RestTemplate(requestFactory);
        final Map<String, String> headers = new HashMap<>(client.getHeaders());
        headers.remove(AUTHORIZATION);
        if (!headers.isEmpty()) {
            template.setInterceptors(singletonList(
                    new GenericRequestHeaderInterceptor(headers)));
        }
        
        return template;
    }
    
    private void addAuthorizationToken(
            final ConfigClientProperties configClientProperties,
            final HttpHeaders httpHeaders, final String username,
            final String password) {
        final String authorization = configClientProperties.getHeaders()
                .get(AUTHORIZATION);
        
        if (password != null && authorization != null) {
            throw new IllegalStateException(
                    "You must set either 'password' or 'authorization'");
        }
        
        if (password != null) {
            final byte[] token = Base64Utils
                    .encode((username + ":" + password).getBytes());
            httpHeaders.add("Authorization", "Basic " + new String(token));
        } else if (authorization != null) {
            httpHeaders.add("Authorization", authorization);
        }
        
    }
    
    /**
     * Adds the provided headers to the request.
     */
    public static class GenericRequestHeaderInterceptor
            implements ClientHttpRequestInterceptor {
        
        private final Map<String, String> headers;
        
        public GenericRequestHeaderInterceptor(
                final Map<String, String> headers) {
            this.headers = headers;
        }
        
        @NonNull
        @Override
        public ClientHttpResponse intercept(@NonNull final HttpRequest request,
                @NonNull final byte[] body,
                @NonNull final ClientHttpRequestExecution execution)
                throws IOException {
            for (final Map.Entry<String, String> header : this.headers
                    .entrySet()) {
                request.getHeaders().add(header.getKey(), header.getValue());
            }
            return execution.execute(request, body);
        }
    }
    
    static class OpenApiConfigServiceOrigin implements Origin {
        
        private final String remotePropertySource;
        
        private final Object origin;
        
        OpenApiConfigServiceOrigin(final String remotePropertySource,
                final Object origin) {
            this.remotePropertySource = remotePropertySource;
            Assert.notNull(origin, "origin may not be null");
            this.origin = origin;
            
        }
        
        @Override
        public String toString() {
            return "Config Server " + this.remotePropertySource + ":"
                    + this.origin.toString();
        }
        
    }
    
    @ConditionalOnProperty("spring.cloud.config.fail-fast")
    @ConditionalOnClass({ Retryable.class, Aspect.class,
            AopAutoConfiguration.class })
    @Configuration(proxyBeanMethods = false)
    @EnableRetry(proxyTargetClass = true)
    @Import(AopAutoConfiguration.class)
    @EnableConfigurationProperties(RetryProperties.class)
    protected static class RetryConfiguration {
        
        @Bean
        @ConditionalOnMissingBean(
                name = OPEN_API_CONFIG_SERVER_RETRY_INTERCEPTOR)
        public RetryOperationsInterceptor openApiConfigServerRetryInterceptor(
                final RetryProperties properties) {
            return RetryInterceptorBuilder.stateless()
                    .backOffOptions(properties.getInitialInterval(),
                            properties.getMultiplier(),
                            properties.getMaxInterval())
                    .maxAttempts(properties.getMaxAttempts()).build();
        }
        
    }
    
}
