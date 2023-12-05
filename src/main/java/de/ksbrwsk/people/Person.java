package de.ksbrwsk.people;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

/**
 * This class represents a Person entity.
 * It includes validation and API documentation annotations.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {
    /**
     * The unique identifier of the person.
     * It is automatically generated.
     */
    @Id
    @Schema(name = "id", description = "The person's id")
    private Long id;

    /**
     * The name of the person.
     * It cannot be blank and its size must be between 1 and 10 characters.
     */
    @NotBlank
    @Size(min = 1, max = 10)
    @Schema(minLength = 1, maxLength = 10, nullable = false, name = "name", description = "The person's name")
    private String name;

    /**
     * Constructor that sets the name of the person.
     * @param name The name of the person.
     */
    public Person(String name) {
        this.name = name;
    }
}