package de.ksbrwsk.people;

import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class PersonTest {
    @Autowired
    Validator validator;

    @Test
    void should_create_person() {
        Person person = new Person(1L, "Name");
        assertThat(person.id()).isEqualTo(1L);
        assertThat(person.name()).isEqualTo("Name");
    }

    @ParameterizedTest
    @ValueSource(strings = {"N", "Name", "0123456789"})
    void should_create_valid_person(String name) {
        Person person = new Person(name);
        var violations = this.validator.validate(person);
        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"01234567890"})
    void should_create_invalid_person(String name) {
        Person person = new Person(name);
        var violations = this.validator.validate(person);
        assertThat(violations).isNotEmpty();
        violations.forEach(System.out::println);
    }
}