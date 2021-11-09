package de.ksbrwsk.people;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static de.ksbrwsk.people.Constants.BASE;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class PersonRouter {
    @Bean
    RouterFunction<ServerResponse> http(PersonHandler personHandler) {
        return nest(path(BASE),
                route(GET(""), personHandler::handleFindAll)
                        .andRoute(GET("/{id}"), personHandler::handleFindById)
                        .andRoute(GET("/firstByName/{name}"), personHandler::handleFindFirstByName)
                        .andRoute(DELETE("/{id}"), personHandler::handleDeleteById)
                        .andRoute(POST(""), personHandler::handleSave)
                        .andRoute(PUT("/{id}"), personHandler::handleUpdate)
        );
    }
}
