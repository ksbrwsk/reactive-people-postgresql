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
import static org.springframework.web.reactive.function.BodyInserters.fromPublisher;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;
import static org.springframework.web.reactive.function.server.ServerResponse.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class PersonHandler {
    private final PersonRepository personRepository;
    private final Validator validator;

    public Mono<ServerResponse> handleFindAll(ServerRequest serverRequest) {
        return ok()
                .body(this.personRepository.findAll(), Person.class);
    }

    public Mono<ServerResponse> handleFindById(ServerRequest serverRequest) {
        var id = Long.parseLong(serverRequest.pathVariable("id"));
        return this.personRepository.findById(id)
                .flatMap(person -> ok()
                        .bodyValue(person))
                .switchIfEmpty(notFound().build());
    }

    public Mono<ServerResponse> handleFindFirstByName(ServerRequest serverRequest) {
        log.info("Handle request {} {}", serverRequest.method(), serverRequest.path());
        var name = serverRequest.pathVariable("name");
        Mono<Person> firstByName = this.personRepository.findFirstByName(name);
        Mono<ServerResponse> notFound = notFound().build();
        return firstByName.flatMap(person -> ok()
                        .body(fromValue(person)))
                .switchIfEmpty(notFound);
    }

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

    public Mono<ServerResponse> handleCreate(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(Person.class)
                .switchIfEmpty(Mono.error(new ServerWebInputException("person must not be null")))
                .doOnNext(this::validate)
                .flatMap(this.personRepository::save)
                .flatMap(person ->
                        created(URI.create(API + "/" + person.getId()))
                                .bodyValue(person));
    }

    public Mono<ServerResponse> handleUpdate(ServerRequest serverRequest) {
        var id = Long.parseLong(serverRequest.pathVariable("id"));
        final Mono<Person> update = serverRequest.bodyToMono(Person.class)
                .doOnNext(this::validate)
                .switchIfEmpty(Mono.error(new ServerWebInputException("person must not be null")));
        return this.personRepository.findById(id)
                .flatMap(old ->
                        ok().body(
                                fromPublisher(
                                        update
                                                .map(p -> new Person(id, p.getName()))
                                                .flatMap(this.personRepository::save),
                                        Person.class)))
                .switchIfEmpty(notFound().build());
    }

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

    private String formatError(ConstraintViolation<Person> personConstraintViolation) {
        String field = StringUtils.capitalize(personConstraintViolation.getPropertyPath().toString());
        String error = personConstraintViolation.getMessage();
        return String.format("%s - %s", field, error);
    }
}
