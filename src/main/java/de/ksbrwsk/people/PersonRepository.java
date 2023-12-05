package de.ksbrwsk.people;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

/**
 * This interface represents a repository for the Person entity.
 * It extends the ReactiveCrudRepository interface provided by Spring Data.
 */
public interface PersonRepository extends ReactiveCrudRepository<Person, Long> {
    /**
     * This method returns the first Person found with the given name.
     * @param name The name of the Person to search for.
     * @return A Mono that emits the found Person or completes without emitting any items if no Person is found.
     */
    Mono<Person> findFirstByName(String name);

    /**
     * This method returns the Person with the lowest id.
     * @return A Mono that emits the found Person or completes without emitting any items if no Person is found.
     */
    Mono<Person> findTopByOrderByIdAsc();
}