package de.ksbrwsk.people;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class PersonRouter {

    private final static String BASE_URL = "/api/people";

    @Bean
    RouterFunction<ServerResponse> http(PersonHandler personHandler) {
        return route()
                .GET(BASE_URL, personHandler::handleFindAll)
                .GET(BASE_URL + "/{id}", personHandler::handleFindById)
                .DELETE(BASE_URL + "/{id}", personHandler::handleDeleteById)
                .POST(BASE_URL, personHandler::handleSave)
                .build();
    }
}
