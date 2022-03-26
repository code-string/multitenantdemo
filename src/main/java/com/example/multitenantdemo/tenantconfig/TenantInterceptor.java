package com.example.multitenantdemo.tenantconfig;

import com.example.multitenantdemo.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;

@Slf4j
@Component
public class TenantInterceptor implements WebRequestInterceptor {

    private static final String TENANT_HEADER = "X-TenantId";

    @Override
    public void preHandle(WebRequest request) throws Exception {
        String tenantId = request.getHeader(TENANT_HEADER);
        if(tenantId == null || tenantId.isBlank()){
            log.info("Tenant header is missing");
            throw new NotFoundException("X-TenantId header missing");
        }
        log.info("Extracting Tenant header {}", tenantId);
        TenantStore.setTenantId(tenantId);
        log.info("Extraction complete....");
    }

    @Override
    public void postHandle(WebRequest request, ModelMap model) throws Exception {
        TenantStore.clear();
        
    }

    @Override
    public void afterCompletion(WebRequest request, Exception ex) throws Exception {

    }
}
