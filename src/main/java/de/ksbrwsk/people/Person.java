package de.ksbrwsk.people;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;

/**
 * This class represents a Person entity.
 * It includes validation and API documentation annotations.
 */
public record Person(
        @Id
        @Schema(name = "id", description = "The person's id")
        Long id,
        @NotBlank
        @Size(min = 1, max = 10)
        @Schema(minLength = 1, maxLength = 10, nullable = false, name = "name", description = "The person's name")
        String name
) {
    /**
     * Constructor that sets the name of the person.
     *
     * @param name The name of the person.
     */
    public Person(String name) {
        this(null, name);
    }
}