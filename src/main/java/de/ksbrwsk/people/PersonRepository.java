package de.ksbrwsk.people;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface PersonRepository extends ReactiveCrudRepository<Person, Long> {
    Mono<Person> findFirstByName(String name);

    Mono<Person> findTopByOrderByIdAsc();
}
