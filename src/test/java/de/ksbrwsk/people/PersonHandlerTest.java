package de.ksbrwsk.people;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static de.ksbrwsk.people.Constants.API;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest
@Import({PersonHandler.class, PersonRouter.class})
@Log4j2
class PersonHandlerTest {

    @Autowired
    WebTestClient webTestClient;

    @MockitoBean
    PersonRepository personRepository;

    @Test
    @DisplayName("should handle request find all")
    void should_handle_find_all() {
        when(this.personRepository.findAll())
                .thenReturn(Flux.just(
                        new Person(1L, "Name"),
                        new Person(2L, "Sabo")
                ));
        this.webTestClient
                .get()
                .uri(API)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Person.class)
                .hasSize(2)
                .contains(
                        new Person(1L, "Name"),
                        new Person(2L, "Sabo"));
    }

    @Test
    @DisplayName("should handle request find by id x")
    void should_handle_find_by_id() {
        when(this.personRepository.findById(1L))
                .thenReturn(Mono.just(new Person(1L, "Name")));
        this.webTestClient
                .get()
                .uri(API + "/1")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Person.class)
                .consumeWith(response -> {
                    assertThat(response.getResponseBody()).isNotNull();
                    assertThat(response.getResponseBody().name()).isEqualTo("Name");
                    assertThat(response.getResponseBody().id()).isEqualTo(1L);
                });
    }

    @Test
    @DisplayName("should handle request find by unknown id x")
    void should_handle_find_by_unknown_id() {
        when(this.personRepository.findById(1000L))
                .thenReturn(Mono.empty());
        this.webTestClient
                .get()
                .uri(API + "/1000")
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    @DisplayName("should handle request delete by id x")
    void should_handle_delete_by_id() {
        Person person = new Person(1L, "Name");
        when(this.personRepository.findById(any(Long.class)))
                .thenReturn(Mono.just(person));
        when(this.personRepository.delete(any(Person.class)))
                .thenReturn(Mono.empty());
        this.webTestClient
                .delete()
                .uri(API + "/1")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBody(String.class)
                .isEqualTo("successfully deleted!");
    }

    @Test
    @DisplayName("should handle request delete by unknown id x")
    void should_handle_delete_by_unknown_id() {
        when(this.personRepository.findById(1L))
                .thenReturn(Mono.empty());
        this.webTestClient
                .delete()
                .uri(API + "/1")
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @ParameterizedTest
    @CsvSource({"N", "Name123456"})
    @DisplayName("should successfully handle request create person")
    void should_handle_create_person(String name) {
        Person person = new Person(1L, name);
        Mono<Person> personMono = Mono.just(person);
        when(this.personRepository.save(person))
                .thenReturn(personMono);
        this.webTestClient
                .post()
                .uri(API)
                .bodyValue(person)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(Person.class)
                .isEqualTo(person);
    }

    @ParameterizedTest
    @CsvSource({"Name1234567"})
    @NullAndEmptySource
    @DisplayName("should successfully handle request create invalid person")
    void should_handle_create_invalid_person(String name) {
        this.webTestClient
                .post()
                .uri(API)
                .bodyValue(new Person(name))
                .exchange()
                .expectStatus()
                .isBadRequest();
    }


    @ParameterizedTest
    @ValueSource(strings = {"Name123456"})
    @DisplayName("should successfully handle request update person")
    void should_handle_update_person(String name) {
        Person person = new Person(1L, name);
        Mono<Person> personMono = Mono.just(person);
        when(this.personRepository.findById(any(Long.class)))
                .thenReturn(Mono.just(new Person(1L, "Name")));
        when(this.personRepository.save(person))
                .thenReturn(personMono);
        this.webTestClient
                .put()
                .uri(API + "/1")
                .bodyValue(person)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Person.class)
                .isEqualTo(person);
    }

    /**
     * Handle validation
     * name is null
     * name is empty
     * name length gt 10
     *
     * @param name the name type String
     */
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"01234567890"})
    void should_handle_person_not_valid(String name) {
        this.webTestClient
                .post()
                .uri(API)
                .bodyValue(new Person(name))
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    @DisplayName("should handle unknown URL")
    void should_handle_not_found() {
        this.webTestClient
                .get()
                .uri("/api/peple")
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    @DisplayName("should handle request find first by name")
    void should_handle_find_first_by_name() {
        when(this.personRepository.findFirstByName(any(String.class)))
                .thenReturn(Mono.just(new Person(1L, "First")));
        this.webTestClient
                .get()
                .uri(API + "/firstByName/First")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Person.class)
                .isEqualTo(new Person(1L, "First"));
    }

    @Test
    @DisplayName("should handle request find first by name not found")
    void should_handle_find_first_by_name_not_found() {
        when(this.personRepository.findFirstByName(any(String.class)))
                .thenReturn(Mono.empty());
        this.webTestClient
                .get()
                .uri(API + "/firstByName/First")
                .exchange()
                .expectStatus()
                .isNotFound();
    }
}