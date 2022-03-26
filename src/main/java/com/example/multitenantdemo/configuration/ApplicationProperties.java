package com.example.multitenantdemo.configuration;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private String serviceKey;
    private String tenantKey;
    private final DatasourceDefault datasourceDefault = new DatasourceDefault();
    private final Encryption encryption = new Encryption();

    @Getter
    @Setter
    public static class DatasourceDefault{
        private String alias;
        private String host;
        private String port;
        private String database;
        private String username;
        private String password;
    }

    @Setter
    @Getter
    public static class Encryption{
        private String secret;
        private String salt;
    }
}
