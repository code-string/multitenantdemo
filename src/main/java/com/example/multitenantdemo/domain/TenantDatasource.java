package com.example.multitenantdemo.domain;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.*;

import java.util.Collections;


@Setter
@Getter
@ToString
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class TenantDatasource {
    private String alias;
    private String host;
    private int port;
    private String database;
    private String username;
    private String password;
    private MongoClient client;

    public TenantDatasource(String alias, String host, int port, String database, String username, String password) {
        this.alias = alias;
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        createClient();
    }

    private void createClient(){
        MongoCredential credential = MongoCredential.createCredential(username, database, password.toCharArray());
        client = MongoClients.create(MongoClientSettings.builder()
                .applyToClusterSettings(builder ->
                        builder.hosts(Collections.singletonList(new ServerAddress(host, port))))
                .credential(credential).build());
    }
}
