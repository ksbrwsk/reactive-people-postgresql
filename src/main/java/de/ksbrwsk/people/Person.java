package de.ksbrwsk.people;

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
    private Long id;

    @NotNull
    @Size(min = 1, max = 10)
    private String name;

    public Person(String name) {
        this.name = name;
    }
}
