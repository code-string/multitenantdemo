package com.example.multitenantdemo.tenantconfig;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TenantStore {
    private static final ThreadLocal<String> context = new ThreadLocal<>();

    public static void setTenantId(String tenantId){
        log.info("Setting tenantId to {}", tenantId);
        context.set(tenantId);
        log.info("TenantId {}, added to local store", tenantId);
    }

    public static String getTenantId(){
        return context.get();
    }

    public static void clear(){
        log.info("Clearing thread for {}", getTenantId());
        context.remove();
        log.info("Thread cleared ......");
    }
}
