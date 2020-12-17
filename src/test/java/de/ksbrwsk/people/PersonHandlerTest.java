package de.ksbrwsk.people;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@WebFluxTest
@ContextConfiguration(classes = {PersonHandler.class, PersonRouter.class})
@Log4j2
class PersonHandlerTest {

    private final static String BASE_URL = "/api/people";

    @Autowired
    ApplicationContext applicationContext;

    @MockBean
    PersonRepository personRepository;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToApplicationContext(applicationContext).build();
    }

    @Test
    @DisplayName("should handle request find all")
    void should_handle_find_all() {
        Mockito
                .when(this.personRepository.findAll())
                .thenReturn(Flux.just(
                        new Person(1L, "Name"),
                        new Person(2L, "Sabo")
                ));

        this.webTestClient
                .get()
                .uri(BASE_URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Person.class)
                .value(people -> {
                    assertEquals(people.get(0).getId(), 1L);
                    assertEquals(people.get(0).getName(), "Name");
                    assertEquals(people.get(1).getId(), 2L);
                    assertEquals(people.get(1).getName(), "Sabo");
                });
    }

    @Test
    @DisplayName("should handle request find by id x")
    void should_handle_find_by_id() {
        Mockito
                .when(this.personRepository.findById(1L))
                .thenReturn(Mono.just(new Person(1L, "Name")));

        Person person = this.webTestClient
                .get()
                .uri(BASE_URL+"/1")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(Person.class)
                .returnResult()
                .getResponseBody();
        assertNotNull(person);
        assertEquals(person.getName(), "Name");
        assertEquals(person.getId(), 1L);
    }

    @Test
    @DisplayName("should handle request find by unknown id x")
    void should_handle_find_by_unknown_id() {
        Mockito
                .when(this.personRepository.findById(1000L))
                .thenReturn(Mono.empty());

        this.webTestClient
                .get()
                .uri(BASE_URL+"/1000")
                .exchange()
                .expectStatus()
                .is4xxClientError();
    }

    @Test
    @DisplayName("should handle request delete by id x")
    void should_handle_delete_by_id() {
        Person person = new Person(1L, "Name");
        Mockito
                .when(this.personRepository.findById(any(Long.class)))
                .thenReturn(Mono.just(person));
        Mockito
                .when(this.personRepository.delete(any(Person.class)))
                .thenReturn(Mono.empty());

        String actual = this.webTestClient
                .delete()
                .uri(BASE_URL+"/1")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        var expected = "successfully deleted!";
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("should handle request save person")
    void should_handle_save_person() {

        Person person = new Person(1L, "Name");
        Mono<Person> personMono = Mono.just(person);

        Mockito
                .when(this.personRepository.save(person))
                .thenReturn(personMono);

        Person result = this.webTestClient
                .post()
                .uri("/api/people")
                .body(fromValue(person))
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(Person.class)
                .returnResult()
                .getResponseBody();
        assertNotNull(result);
        assertEquals(person, result);
    }

    @Test
    @DisplayName("should handle unknwon URL")
    void should_handle_not_found() {
        this.webTestClient
                .get()
                .uri("/api/peple")
                .exchange()
                .expectStatus()
                .is4xxClientError();
    }
}