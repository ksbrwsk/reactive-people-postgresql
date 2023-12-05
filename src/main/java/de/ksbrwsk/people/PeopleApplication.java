package de.ksbrwsk.people;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

/**
 * This is the main class for the PeopleApplication.
 * It uses the @SpringBootApplication annotation to enable auto-configuration and component scanning.
 * It also uses the @EnableR2dbcRepositories annotation to enable the creation of reactive repositories.
 * The @OpenAPIDefinition annotation is used to provide metadata for the OpenAPI documentation.
 */
@SpringBootApplication
@EnableR2dbcRepositories
@OpenAPIDefinition(info = @Info(
        title = "reactive-people-postgresql",
        version = "1.1",
        description = "Spring WebFlux CRUD Example Sample documents"
))
public class PeopleApplication {

    /**
     * The main method that starts the application.
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        SpringApplication.run(PeopleApplication.class, args);
    }
}