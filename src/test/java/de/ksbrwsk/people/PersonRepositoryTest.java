package de.ksbrwsk.people;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataR2dbcTest
class PersonRepositoryTest extends AbstractIntegrationTest{

    @Autowired
    PersonRepository personRepository;

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
                .expectNextMatches(person -> person.getId() != null &&
                        person.getName().equalsIgnoreCase("name"))
                .expectNextMatches(person -> person.getId() != null &&
                        person.getName().equalsIgnoreCase("sabo"))
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
                .flatMap(person -> this.personRepository.findById(person.getId()));
        StepVerifier
                .create(personFlux)
                .expectNextMatches(person -> person.getName().equalsIgnoreCase("sabo"))
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
                .expectNextMatches(person -> person.getId() != null &&
                        person.getName().equalsIgnoreCase("sabo"))
                .verifyComplete();
    }

    @Test
    @DisplayName("should delete person by id x")
    void should_delete_person_by_id() {
        Mono<Person> personFlux = this.personRepository
                .deleteAll()
                .then(this.personRepository.save(new Person(null, "Name")))
                .then(this.personRepository.save(new Person(null, "Sabo")))
                .then(this.personRepository.deleteById(2L))
                .then(this.personRepository.findById(2L));
        StepVerifier
                .create(personFlux)
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