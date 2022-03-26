package com.example.multitenantdemo.configuration;

import com.example.multitenantdemo.domain.TenantDatasource;
import com.example.multitenantdemo.exception.NotFoundException;
import com.example.multitenantdemo.service.RedisDatasourceService;
import com.example.multitenantdemo.tenantconfig.TenantStore;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Map;

@Component
@Slf4j
public class MongoDataSources {
    private Map<String, TenantDatasource> tenants;

    private final ApplicationProperties applicationProperties;
    private final RedisDatasourceService redisDatasourceService;

    public MongoDataSources(ApplicationProperties applicationProperties, RedisDatasourceService redisDatasourceService) {
        this.applicationProperties = applicationProperties;
        this.redisDatasourceService = redisDatasourceService;
    }

    /**
     * Initializes all mongo datasource
     * */
    @Lazy
    @PostConstruct
    public void initTenant(){
        tenants = redisDatasourceService.loadServiceDatasources();
    }

    /**
     * Default database name for spring initialization.
     * To be injected into MultiTenantMongoFactory constructor
     * */
    @Bean
    public String databaseName(){
        log.info("Fetching tenant database name ....");
        return applicationProperties.getDatasourceDefault().getDatabase();
    }

    /**
     * Default Mongo connection for spring initialization.
     * To be injected into MultiTenantMongoFactory constructor
     * */
    @Bean
    public MongoClient getMongoClient(){
        log.info("Creating mongo client ....");
        MongoCredential credential = MongoCredential.createCredential(applicationProperties.getDatasourceDefault().getUsername(), applicationProperties.getDatasourceDefault().getDatabase(), applicationProperties.getDatasourceDefault().getPassword().toCharArray());
        return MongoClients.create(MongoClientSettings.builder()
                .applyToClusterSettings(builder -> builder.hosts(Collections.singletonList(new ServerAddress(applicationProperties.getDatasourceDefault().getHost(), Integer.parseInt(applicationProperties.getDatasourceDefault().getPort())))))
                .credential(credential).build());
    }

    public MongoDatabase mongoDatabaseCurrentTenantResolver(){
        try{
            log.info("resolving tenant data source ....");
            final String tenantId = TenantStore.getTenantId();
            String tenantAlias = String.format("%s_%s", applicationProperties.getTenantKey(), tenantId);
            return tenants.get(tenantAlias).getClient().getDatabase(tenants.get(tenantAlias).getDatabase());
        }catch (NullPointerException e){
            throw  new NotFoundException("Tenant Datasource alias not found");
        }
    }
}
