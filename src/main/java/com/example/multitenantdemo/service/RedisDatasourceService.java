package com.example.multitenantdemo.service;

import com.example.multitenantdemo.configuration.ApplicationProperties;
import com.example.multitenantdemo.configuration.DatasourceProperties;
import com.example.multitenantdemo.domain.TenantDatasource;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class stores all information of tenant databases.
 * */
@Slf4j
@Service
public class RedisDatasourceService {
    private final RedisTemplate redisTemplate;
    private final ApplicationProperties applicationProperties;
    private final DatasourceProperties datasourceProperties;
    private final EncryptionService encryptionService;


    public RedisDatasourceService(RedisTemplate redisTemplate, ApplicationProperties applicationProperties, DatasourceProperties datasourceProperties, EncryptionService encryptionService) {
        this.redisTemplate = redisTemplate;
        this.applicationProperties = applicationProperties;
        this.datasourceProperties = datasourceProperties;
        this.encryptionService = encryptionService;
    }

    /**
     * @param tenantDatasource data of datasource
     * @return status if true: saved successfully, false error occurred
     * */
    public boolean save(TenantDatasource tenantDatasource){
        try{
            Map ruleHash = new ObjectMapper().convertValue(tenantDatasource, Map.class);
            redisTemplate.opsForHash().put(applicationProperties.getServiceKey(), String.format("%s_%s", applicationProperties.getTenantKey(),
                    tenantDatasource.getAlias()), ruleHash);
            log.info(String.format("%s_%s", applicationProperties.getTenantKey(),
                    tenantDatasource.getAlias()) + " <<<<<<<<<<<<<<<=====================>>>>>>>>>>>>>>>>>>>>");
            return true;
        }catch (Exception e){
            return false;
        }
    }
    /**
     * @return list of datasources
     * */
    public List findAll(){
        log.info("datasource list {}", redisTemplate.opsForHash().values(applicationProperties.getServiceKey()));
        return redisTemplate.opsForHash().values(applicationProperties.getServiceKey());
    }

    public Map<String, TenantDatasource> loadServiceDatasources(){
        log.info("Started loading datasources ......");
        List<String> aliases = getTenantAliases();
        List<String> keys = aliases.stream().map(d -> String.format("%s_%s", applicationProperties.getTenantKey(), d)).collect(Collectors.toList());

        Boolean some = redisTemplate.delete(applicationProperties.getServiceKey());
        log.info("redis keys deleted? ------>{}", some);
        List<Map<String, Object>> datasourceConfigList = findAll();
        if(datasourceConfigList.isEmpty()){
            log.info("No tenant datasource options found ....");
            log.info("Attempting to save new tenants ....");
            List<DatasourceProperties.Tenant> tenants = datasourceProperties.getDatasources();
            tenants.forEach( d -> {
                log.info("tenant ======> {}", d.getAlias());
                String encryptedPassword = encryptionService.encrypt(d.getPassword(), applicationProperties.getEncryption().getSecret(), applicationProperties.getEncryption().getSalt());
                TenantDatasource tenant = TenantDatasource.builder()
                        .alias(d.getAlias())
                        .database(d.getDatabase())
                        .host(d.getHost())
                        .password(encryptedPassword)
                        .username(d.getUsername())
                        .port(d.getPort())
                        .build();
                save(tenant);
            });
        }
        return getDataSourceHashMap();
    }

    /***
     *
     * @return Map<String, TenantDatasource>
     */
    public Map<String, TenantDatasource> getDataSourceHashMap(){
        Map<String, TenantDatasource> datasourceMap = new HashMap<>();
        List<Map<String, Object>> datasourceConfigList = findAll();

        datasourceConfigList.forEach(config -> {
            String decryptedPassword = encryptionService.decrypt((String) config.get("password"), applicationProperties.getEncryption().getSecret(), applicationProperties.getEncryption().getSalt());
            datasourceMap.put(String.format("%s_%s", applicationProperties.getTenantKey(), config.get("alias")), new TenantDatasource((String)config.get("alias"), (String)config.get("host"), (int)config.get("port"), (String)config.get("database"), (String)config.get("username"), decryptedPassword));
        });
        return datasourceMap;
    }
    /***
     * @return list of tenant alias
     */
    public List<String> getTenantAliases(){
        List<Map<String, Object>> datasourceConfigList = findAll();
        return datasourceConfigList.stream().map(config -> (String)config.get("alias")).collect(Collectors.toList());
    }
}
