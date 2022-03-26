package com.example.multitenantdemo;

import com.example.multitenantdemo.domain.Employee;
import com.example.multitenantdemo.repository.EmployeeRepository;
import com.example.multitenantdemo.service.RedisDatasourceService;
import com.example.multitenantdemo.tenantconfig.TenantStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.List;

@Slf4j
@SpringBootApplication
@EnableConfigurationProperties
public class MultitenantdemoApplication implements CommandLineRunner {

    @Autowired
    private RedisDatasourceService redisDatasourceService;

    @Autowired
    private EmployeeRepository employeeRepository;

    public static void main(String[] args) {
        SpringApplication.run(MultitenantdemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("redis store initialization starting...");
        List<String> aliasList = redisDatasourceService.getTenantAliases();
        log.info("Got all aliases---------->{}", aliasList);
        if(!aliasList.isEmpty()){
            aliasList.forEach(alias -> {
                TenantStore.setTenantId(alias);
                employeeRepository.deleteAll();

                Employee employee = Employee.builder()
                        .firstName("Johnny")
                        .lastName("English")
                        .email(String.format("John%s@localhost.com", alias))
                        .build();
                Employee employee2 = Employee.builder()
                        .firstName("Jude")
                        .lastName("Uzoaru")
                        .email(String.format("Jude%s@localhost.com", alias))
                        .build();
                employeeRepository.saveAll(List.of(employee, employee2));
                TenantStore.clear();
            });
        }else{
            log.info("Alias list is empty");
        }
    }
}
