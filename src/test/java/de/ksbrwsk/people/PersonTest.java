package de.ksbrwsk.people;

import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(properties = {"spring.autoconfigure.exclude=" +
        "org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration"})
class PersonTest {
    @Autowired
    Validator validator;

    @Test
    void should_create_person() {
        Person person = new Person(1L, "Name");
        assertEquals(person.getId(), 1L);
        assertEquals(person.getName(), "Name");
    }


    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"01234567890"})
    void notValid(String name) {
        Person person = new Person(name);
        var violations = this.validator.validate(person);
        assertFalse(violations.isEmpty());
        violations.forEach(System.out::println);
    }
}