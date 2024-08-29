package de.ksbrwsk.people;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataR2dbcTest
class PersonRepositoryTest extends PostgreSqlContainer {

    @Autowired
    PersonRepository personRepository;

    private Person findFirst() {
        var first = this.personRepository
                .findTopByOrderByIdAsc()
                .block();
        return first;
    }

    @Test
    @DisplayName("should persist people")
    void should_persist_people() {
        Flux<Person> personFlux = this.personRepository
                .deleteAll()
                .then(this.personRepository.save(new Person(null, "Name")))
                .then(this.personRepository.save(new Person(null, "Sabo")))
                .thenMany(this.personRepository.findAll());
        StepVerifier
                .create(personFlux)
                .expectNextMatches(person -> person.id() != null &&
                        person.name().equalsIgnoreCase("name"))
                .expectNextMatches(person -> person.id() != null &&
                        person.name().equalsIgnoreCase("sabo"))
                .verifyComplete();
    }

    @Test
    @DisplayName("should find person by id x")
    void should_find_person_by_id() {
        Mono<Person> personFlux = this.personRepository
                .deleteAll()
                .then(this.personRepository.save(new Person(null, "Name")))
                .then(this.personRepository.save(new Person(null, "Sabo")))
                .then(this.personRepository.findFirstByName("Sabo"))
                .flatMap(person -> this.personRepository.findById(person.id()));
        StepVerifier
                .create(personFlux)
                .expectNextMatches(person -> person.name().equalsIgnoreCase("sabo"))
                .verifyComplete();
    }

    @Test
    @DisplayName("should find first person by name x")
    void should_find_first_person_by_name() {
        Mono<Person> personFlux = this.personRepository
                .deleteAll()
                .then(this.personRepository.save(new Person(null, "Name")))
                .then(this.personRepository.save(new Person(null, "Sabo")))
                .then(this.personRepository.findFirstByName("Sabo"));
        StepVerifier
                .create(personFlux)
                .expectNextMatches(person -> person.id() != null &&
                        person.name().equalsIgnoreCase("sabo"))
                .verifyComplete();
    }

    @Test
    @DisplayName("should delete person by id x")
    void should_delete_person_by_id() {
        this.personRepository
                .deleteAll()
                .then(this.personRepository.save(new Person(null, "Name")))
                .block();
        Person first = this.findFirst();
        Mono<Long> longMono = this.personRepository
                .findById(first.id())
                .flatMap(this.personRepository::delete)
                .then(this.personRepository.count());
        StepVerifier
                .create(longMono)
                .expectNext(0L)
                .verifyComplete();
    }

    @Test
    @DisplayName("should be empty result")
    void should_be_empty_result() {
        Mono<Person> byId = this.personRepository.findById(11111111L);
        StepVerifier
                .create(byId)
                .verifyComplete();
    }
}