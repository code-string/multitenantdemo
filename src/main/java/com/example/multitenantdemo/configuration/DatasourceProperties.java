package com.example.multitenantdemo.configuration;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

@Data
@Component
@ConfigurationProperties(prefix = "tenants")
@PropertySource(value = "classpath:tenants.yml", factory=YamlPropertySourceFactory.class)
public class DatasourceProperties {

    private List<Tenant> datasources = new ArrayList<>();

    @Getter
    @Setter
    public static class Tenant{
        private String alias;
        private String host;
        private int port;
        private String database;
        private String username;
        private String password;
    }
}

class YamlPropertySourceFactory implements PropertySourceFactory{

    @Override
    public PropertiesPropertySource createPropertySource(@Nullable String name, EncodedResource resource) throws IOException {
        Properties properties = loadYamlIntoProperties(resource);
        String sourceName = name != null ? name : resource.getResource().getFilename();
        return new PropertiesPropertySource(Objects.requireNonNull(sourceName), properties);
    }

    private Properties loadYamlIntoProperties(EncodedResource resource) throws FileNotFoundException{
        try{
            YamlPropertiesFactoryBean factoryBean = new YamlPropertiesFactoryBean();
            factoryBean.setResources(resource.getResource());
            factoryBean.afterPropertiesSet();
            return factoryBean.getObject();
        }catch (IllegalStateException e){
            Throwable cause = e.getCause();
            if(cause instanceof FileNotFoundException)
                throw (FileNotFoundException) e.getCause();
            throw e;
        }
    }
}
