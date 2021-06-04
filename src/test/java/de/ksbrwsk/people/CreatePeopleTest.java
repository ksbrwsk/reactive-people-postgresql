package de.ksbrwsk.people;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

@DataR2dbcTest
@Log4j2
public class CreatePeopleTest extends AbstractIntegrationTest {

    @Autowired
    PersonRepository personRepository;

    @Test
    @DisplayName("should create people")
    void should_create_people() {
        List<Person> people = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            people.add(new Person(null, "Person@" + i));
        }
        Flux<Person> personFlux = this.personRepository
                .deleteAll()
                .thenMany(this.personRepository.saveAll(people));
        StepVerifier
                .create(personFlux)
                .expectNextCount(100L)
                .verifyComplete();
    }
}

