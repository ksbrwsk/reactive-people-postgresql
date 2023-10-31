package de.ksbrwsk.people;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.ksbrwsk.people.Constants.API;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestReactivePeoplePostgresqlApplication.class)
public class RestDocsTest extends PostgreSqlContainer {

    @Autowired
    PersonRepository personRepository;
    private WebTestClient webTestClient;

    @RegisterExtension
    final RestDocumentationExtension restDocumentation = new RestDocumentationExtension();

    private Person fetchFirst() {
        var person = this.personRepository
                .findTopByOrderByIdAsc()
                .block();
        return person;
    }

    @BeforeEach
    public void setUp(ApplicationContext applicationContext, RestDocumentationContextProvider restDocumentation) {
        this.webTestClient = WebTestClient.bindToApplicationContext(applicationContext).configureClient()
                .filter(documentationConfiguration(restDocumentation))
                .build();

        List<Person> people = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            people.add(new Person("Person@" + i));
        }
        Flux<Person> personFlux = this.personRepository
                .deleteAll()
                .thenMany(this.personRepository.saveAll(people));
        StepVerifier
                .create(personFlux)
                .expectNextCount(100L)
                .verifyComplete();
    }

    public List<String> constraintDescriptionForProperty(String property) {
        ConstraintDescriptions userConstraints = new ConstraintDescriptions(Person.class);
        return userConstraints.descriptionsForProperty(property);
    }

    @Test
    void handleNotFound() {
        this.webTestClient
                .get()
                .uri("/api/peple")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .consumeWith(document("not-found"));
    }

    @Test
    void handleFindAll() {
        this.webTestClient
                .get()
                .uri(API)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.[0].id").exists()
                .jsonPath("$.[0].name").isEqualTo("Person@1")
                .jsonPath("$.[1].id").exists()
                .jsonPath("$.[1].name").isEqualTo("Person@2")
                .consumeWith(document("handle-find-all",
                        responseFields(
                                fieldWithPath("[].id")
                                        .type(JsonFieldType.NUMBER)
                                        .description("The person's id")
                                        .attributes(key("constraints").value(constraintDescriptionForProperty("id"))),
                                fieldWithPath("[].name")
                                        .type(JsonFieldType.STRING)
                                        .description("The person's name")
                                        .attributes(key("constraints").value(constraintDescriptionForProperty("name"))))));
    }

    @Test
    void handleFindById() {
        Person first = this.fetchFirst();
        this.webTestClient
                .get()
                .uri(API + "/" + first.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(first.getId())
                .jsonPath("$.name").isEqualTo(first.getName())
                .consumeWith(document("handle-find-by-id",
                        responseFields(
                                fieldWithPath("id")
                                        .type(JsonFieldType.NUMBER)
                                        .description("The person's id")
                                        .attributes(key("constraints").value(constraintDescriptionForProperty("id"))),
                                fieldWithPath("name")
                                        .type(JsonFieldType.STRING)
                                        .description("The person's name")
                                        .attributes(key("constraints").value(constraintDescriptionForProperty("name"))))));
    }

    @Test
    void handleDeleteById() {
        Person first = this.fetchFirst();
        this.webTestClient
                .delete()
                .uri(API + "/" + first.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$").isEqualTo("successfully deleted!")
                .consumeWith(document("handle-delete-by-id"));
    }

    @Test
    void handleFindByIdNotFound() {
        this.webTestClient
                .get()
                .uri(API + "/9999999")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .consumeWith(document("handle-find-by-id-not-found"));
    }

    @Test
    void handleDeleteByIdNotFound() {
        this.webTestClient
                .delete()
                .uri(API + "/9999999")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .consumeWith(document("handle-delete-by-id-not-found"));
    }

    @Test
    void handleUpdate() {
        Person first = this.fetchFirst();
        this.webTestClient
                .put()
                .uri(API + "/" + first.getId())
                .bodyValue(new Person(first.getId(), "Update"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(first.getId())
                .jsonPath("$.name").isEqualTo("Update")
                .consumeWith(document("handle-update",
                        responseFields(
                                fieldWithPath("id")
                                        .type(JsonFieldType.NUMBER)
                                        .description("The person's id")
                                        .attributes(key("constraints").value(constraintDescriptionForProperty("id"))),
                                fieldWithPath("name")
                                        .type(JsonFieldType.STRING)
                                        .description("The person's name")
                                        .attributes(key("constraints").value(constraintDescriptionForProperty("name"))))));
    }

    @Test
    void handleUpdateInvalid() {
        Person first = this.fetchFirst();
        this.webTestClient
                .put()
                .uri(API + "/" + first.getId())
                .bodyValue(new Person(first.getId(), ""))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").exists()
                .consumeWith(document("handle-update-invalid"));
    }

    @Test
    void handleUpdateNotFound() {
        this.webTestClient
                .put()
                .uri(API + "/999999999")
                .bodyValue(new Person(999999999L, "Update"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .consumeWith(document("handle-update-not-found"));
    }

    @Test
    void handleUpdateBadRequest() {
        Person first = this.fetchFirst();
        this.webTestClient
                .put()
                .uri(API + "/" + first.getId())
                .bodyValue(Optional.empty())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(document("handle-update-bad-request"));
    }

    @Test
    void handleCreate() {
        this.webTestClient
                .post()
                .uri(API)
                .bodyValue(new Person("New-Person"))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader()
                .exists("Location")
                .expectBody()
                .consumeWith(document("handle-create",
                        responseFields(
                                fieldWithPath("id")
                                        .type(JsonFieldType.NUMBER)
                                        .description("The person's id")
                                        .attributes(key("constraints").value(constraintDescriptionForProperty("id"))),
                                fieldWithPath("name")
                                        .type(JsonFieldType.STRING)
                                        .description("The person's name")
                                        .attributes(key("constraints").value(constraintDescriptionForProperty("name"))))));
    }

    @Test
    void handleCreateBadRequest() {
        this.webTestClient
                .post()
                .uri(API)
                .bodyValue(Optional.empty())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(document("handle-create-bad-request"));
    }

    @Test
    void handleCreateInvalid() {
        this.webTestClient
                .post()
                .uri(API)
                .bodyValue(new Person(""))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(document("handle-create-invalid"));
    }

}
