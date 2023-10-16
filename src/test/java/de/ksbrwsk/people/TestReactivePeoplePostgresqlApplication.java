package de.ksbrwsk.people;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestReactivePeoplePostgresqlApplication {

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:15.4-alpine"))
                .withReuse(true)
                .withInitScript("schema.sql");
    }

    public static void main(String[] args) {
        SpringApplication.from(PeopleApplication::main).with(TestReactivePeoplePostgresqlApplication.class).run(args);
    }

}
