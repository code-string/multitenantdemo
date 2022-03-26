package com.example.multitenantdemo.configuration;

import com.example.multitenantdemo.tenantconfig.TenantInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Slf4j
public class WebConfiguration implements WebMvcConfigurer {

    private final TenantInterceptor interceptor;

    public WebConfiguration(TenantInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("Adding interceptor to registry .....");
        registry.addWebRequestInterceptor(interceptor);
        log.info("Interceptor added successfully");
    }
}
