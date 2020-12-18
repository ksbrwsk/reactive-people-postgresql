package de.ksbrwsk.people;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@DataR2dbcTest
@Log4j2
public class CreatePeopleTest {
/*
    private final AtomicLong counter = new AtomicLong(1L);

    @Autowired
    PersonRepository personRepository;

    @Test
    @Disabled
    @DisplayName("should create people")
    void should_create_people() {
        List<Person> people = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            people.add(new Person(null, "Person@"+counter.getAndIncrement()));
        }
        this.personRepository.saveAll(people).blockLast();
    }
 */
}

