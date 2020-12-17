package de.ksbrwsk.people;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PersonTest {

    @Test
    void should_create_person() {
        Person p = new Person(1L, "Name");
        assertEquals(p.getId(), 1L);
        assertEquals(p.getName(), "Name");
    }
}