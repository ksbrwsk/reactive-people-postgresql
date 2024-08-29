package de.ksbrwsk.people;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.Set;

import static de.ksbrwsk.people.Constants.API;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;
import static org.springframework.web.reactive.function.server.ServerResponse.*;

/**
 * This class is responsible for handling HTTP requests related to the Person entity.
 * It includes methods for CRUD operations and finding a person by name.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PersonHandler {
    private final PersonRepository personRepository;
    private final Validator validator;

    /**
     * Handles a request to get all persons.
     *
     * @param serverRequest The incoming server request.
     * @return A ServerResponse with the list of all persons.
     */
    public Mono<ServerResponse> handleFindAll(ServerRequest serverRequest) {
        return ok()
                .body(this.personRepository.findAll(), Person.class);
    }

    /**
     * Handles a request to get a person by id.
     *
     * @param serverRequest The incoming server request.
     * @return A ServerResponse with the person found or a 404 status if not found.
     */
    public Mono<ServerResponse> handleFindById(ServerRequest serverRequest) {
        var id = Long.parseLong(serverRequest.pathVariable("id"));
        return this.personRepository.findById(id)
                .flatMap(person -> ok()
                        .bodyValue(person))
                .switchIfEmpty(notFound().build());
    }

    /**
     * Handles a request to get the first person found by name.
     *
     * @param serverRequest The incoming server request.
     * @return A ServerResponse with the person found or a 404 status if not found.
     */
    public Mono<ServerResponse> handleFindFirstByName(ServerRequest serverRequest) {
        log.info("Handle request {} {}", serverRequest.method(), serverRequest.path());
        var name = serverRequest.pathVariable("name");
        Mono<Person> firstByName = this.personRepository.findFirstByName(name);
        Mono<ServerResponse> notFound = notFound().build();
        return firstByName.flatMap(person -> ok()
                        .body(fromValue(person)))
                .switchIfEmpty(notFound);
    }

    /**
     * Handles a request to delete a person by id.
     *
     * @param serverRequest The incoming server request.
     * @return A ServerResponse with a success message or a 404 status if not found.
     */
    public Mono<ServerResponse> handleDeleteById(ServerRequest serverRequest) {
        var id = Long.parseLong(serverRequest.pathVariable("id"));
        return this.personRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "person not found")))
                .flatMap(this.personRepository::delete)
                .thenReturn("successfully deleted!")
                .flatMap(msg -> ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(msg));
    }

    /**
     * Handles a request to create a new person.
     *
     * @param serverRequest The incoming server request.
     * @return A ServerResponse with the created person or a 400 status if the request body is invalid.
     */
    public Mono<ServerResponse> handleCreate(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(Person.class)
                .switchIfEmpty(Mono.error(new ServerWebInputException("person must not be null")))
                .doOnNext(this::validate)
                .flatMap(this.personRepository::save)
                .flatMap(person ->
                        created(URI.create(API + "/" + person.getId()))
                                .bodyValue(person));
    }

    /**
     * Handles a request to update a person by id.
     *
     * @param serverRequest The incoming server request.
     * @return A ServerResponse with the updated person or a 404 status if not found.
     */
    public Mono<ServerResponse> handleUpdate(ServerRequest serverRequest) {
        var id = Long.parseLong(serverRequest.pathVariable("id"));
        var update = serverRequest.bodyToMono(Person.class)
                .doOnNext(this::validate)
                .switchIfEmpty(Mono.error(new ResponseStatusException(BAD_REQUEST, "Body is required")));
        return this.personRepository.findById(id)
                .zipWith(update, (person, personUpdate) -> new Person(person.getId(), personUpdate.getName()))
                .flatMap(this.personRepository::save)
                .flatMap(person -> ServerResponse.ok().bodyValue(person))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    /**
     * Validates a person using the Validator.
     *
     * @param person The person to validate.
     * @throws ServerWebInputException If the person is not valid.
     */
    private void validate(Person person) {
        Set<ConstraintViolation<Person>> violations = this.validator.validate(person);
        if (!violations.isEmpty()) {
            List<String> errors = violations.stream()
                    .map(this::formatError)
                    .toList();
            log.info("person not valid -> {}", errors);
            throw new ServerWebInputException(errors.toString());
        }
    }

    /**
     * Formats a validation error.
     *
     * @param personConstraintViolation The validation error.
     * @return A string representation of the validation error.
     */
    private String formatError(ConstraintViolation<Person> personConstraintViolation) {
        String field = StringUtils.capitalize(personConstraintViolation.getPropertyPath().toString());
        String error = personConstraintViolation.getMessage();
        return String.format("%s - %s", field, error);
    }
}