package de.ksbrwsk.people;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static de.ksbrwsk.people.Constants.API;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * This class is responsible for routing HTTP requests to the appropriate handler methods.
 * It uses Spring's functional routing style with RouterFunctions and RequestPredicates.
 */
@Configuration
public class PersonRouter {
    /**
     * This method defines the routes for the Person API.
     * It uses the @RouterOperations annotation to provide OpenAPI documentation for each route.
     *
     * @param personHandler The handler class that contains the methods to handle the requests.
     * @return A RouterFunction that routes requests to the appropriate handler methods.
     */
    @Bean
    @RouterOperations(
            {
                    @RouterOperation(
                            path = API,
                            produces = {
                                    MediaType.APPLICATION_JSON_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = PersonHandler.class,
                            beanMethod = "handleFindAll",
                            operation = @Operation(
                                    operationId = "handleFindAll",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(
                                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                            array = @ArraySchema(
                                                                    schema = @Schema(implementation = Person.class))
                                                    )
                                            )
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = API + "/{id}",
                            produces = {
                                    MediaType.APPLICATION_JSON_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = PersonHandler.class,
                            beanMethod = "handleFindById",
                            operation = @Operation(
                                    operationId = "handleFindById",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = Person.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "person not found with given id")
                                    },
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "id")
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = API + "/firstByName/{name}",
                            produces = {
                                    MediaType.APPLICATION_JSON_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = PersonHandler.class,
                            beanMethod = "handleFindFirstByName",
                            operation = @Operation(
                                    operationId = "handleFindFirstByName",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = Person.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "no person found with given name")
                                    },
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "name")
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = API + "/{id}",
                            produces = {
                                    MediaType.APPLICATION_JSON_VALUE
                            },
                            method = RequestMethod.DELETE,
                            beanClass = PersonHandler.class,
                            beanMethod = "handleDeleteById",
                            operation = @Operation(
                                    operationId = "handleDeleteById",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = String.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "person not found with given id")
                                    },
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "id")
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = API + "/{id}",
                            produces = {
                                    MediaType.APPLICATION_JSON_VALUE
                            },
                            method = RequestMethod.PUT,
                            beanClass = PersonHandler.class,
                            beanMethod = "handleUpdate",
                            operation = @Operation(
                                    operationId = "handleUpdate",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = Person.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "person not found with given id"),
                                            @ApiResponse(responseCode = "400", description = "person not not valid")
                                    },
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "id")
                                    },
                                    requestBody = @RequestBody(
                                            content = @Content(
                                                    schema = @Schema(implementation = Person.class)
                                            )
                                    )
                            )
                    ),
                    @RouterOperation(
                            path = API,
                            produces = {
                                    MediaType.APPLICATION_JSON_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = PersonHandler.class,
                            beanMethod = "handleCreate",
                            operation = @Operation(
                                    operationId = "handleCreate",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "201",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = Person.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "400", description = "person not not valid")
                                    },
                                    requestBody = @RequestBody(
                                            content = @Content(
                                                    schema = @Schema(implementation = Person.class)
                                            )
                                    )
                            )
                    )
            }
    )
    RouterFunction<ServerResponse> http(PersonHandler personHandler) {
        return nest(path(API),
                route(GET(""), personHandler::handleFindAll)
                        .andRoute(GET("/{id}"), personHandler::handleFindById)
                        .andRoute(GET("/firstByName/{name}"), personHandler::handleFindFirstByName)
                        .andRoute(DELETE("/{id}"), personHandler::handleDeleteById)
                        .andRoute(POST(""), personHandler::handleCreate)
                        .andRoute(PUT("/{id}"), personHandler::handleUpdate)
        );
    }
}
