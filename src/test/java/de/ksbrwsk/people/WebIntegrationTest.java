package de.ksbrwsk.people;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Optional;

import static de.ksbrwsk.people.Constants.API;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebIntegrationTest extends PostgreSqlContainer {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    PersonRepository personRepository;

    @BeforeEach
    public void setUp() {
        log.info("Running setUp -> creatíng 100 people");
        ArrayList<Person> people = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            people.add(new Person("Person@" + i));
        }
        Flux<Person> personFlux = personRepository
                .deleteAll()
                .thenMany(personRepository.saveAll(people));
        StepVerifier
                .create(personFlux)
                .expectNextCount(100L)
                .verifyComplete();
    }

    Person fetchFirstPerson() {
        var person = this.personRepository
                .findTopByOrderByIdAsc()
                .block();
        log.info("First Person found: {}", person);
        return person;
    }

    @ParameterizedTest
    @ValueSource(strings = {"N", "Name", "0123456789"})
    void handleUpdateValid(String name) {
        Person first = this.fetchFirstPerson();
        var id = first.getId();
        this.webTestClient
                .put()
                .uri(API + "/" + id)
                .bodyValue(new Person(id, name))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo(name);
    }

    @ParameterizedTest
    @ValueSource(strings = {"00123456789"})
    @NullAndEmptySource
    void handleUpdateInvalid(String name) {
        Person first = this.fetchFirstPerson();
        var id = first.getId();
        this.webTestClient
                .put()
                .uri(API + "/" + id)
                .bodyValue(new Person(id, name))
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    void handleUpdateNotFound() {
        this.webTestClient
                .put()
                .uri(API + "/10000000")
                .bodyValue(new Person(10000000L, "Update"))
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void handleUpdateBadRequest() {
        Person person = fetchFirstPerson();
        var id = person.getId();
        this.webTestClient
                .put()
                .uri(API + "/" + id)
                .bodyValue(Optional.empty())
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    void handleCreate() {
        this.webTestClient
                .post()
                .uri(API)
                .bodyValue(new Person("Name"))
                .exchange()
                .expectStatus()
                .isCreated()
                .expectHeader()
                .exists("Location")
                .expectBody()
                .jsonPath("$.name").isEqualTo("Name");
    }

    @ParameterizedTest
    @ValueSource(strings = {"N", "Name", "0123456789"})
    void handleCreateValid(String name) {
        this.webTestClient
                .post()
                .uri(API)
                .bodyValue(new Person(name))
                .exchange()
                .expectStatus()
                .isCreated()
                .expectHeader()
                .exists("Location")
                .expectBody()
                .jsonPath("$.name").isEqualTo(name);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"00123456789"})
    void handleCreateInvalid(String name) {
        this.webTestClient
                .post()
                .uri(API)
                .bodyValue(new Person(name))
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    void handleDeleteById() {
        Person first = this.fetchFirstPerson();
        var id = first.getId();
        this.webTestClient
                .delete()
                .uri(API + "/" + id)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$").isEqualTo("successfully deleted!");
    }

    @Test
    void handleFindAll() {
        this.webTestClient
                .get()
                .uri(API)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$[0].id").exists()
                .jsonPath("$[0].name").isEqualTo("Person@1")
                .jsonPath("$[1].id").exists()
                .jsonPath("$[1].name").isEqualTo("Person@2");
    }

    @Test
    void handleNotFound() {
        this.webTestClient
                .get()
                .uri("/api/peple")
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void handleFindById() {
        Person first = this.fetchFirstPerson();
        var id = first.getId();
        this.webTestClient
                .get()
                .uri(API + "/" + id)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(id.toString())
                .jsonPath("$.name").isEqualTo(first.getName());
    }

    @Test
    void handleFindByIdNotFound() {
        this.webTestClient
                .get()
                .uri(API + "/10000000000")
                .exchange()
                .expectStatus()
                .isNotFound();
    }
}
