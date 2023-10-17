package de.ksbrwsk.people;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {
    @Id
    @Schema(name = "id", description = "The person's id")
    private Long id;

    @NotNull
    @Size(min = 1, max = 10)
    @Schema(minLength = 1, maxLength = 10, nullable = false, name = "name", description = "The person's name")
    private String name;

    public Person(String name) {
        this.name = name;
    }
}
