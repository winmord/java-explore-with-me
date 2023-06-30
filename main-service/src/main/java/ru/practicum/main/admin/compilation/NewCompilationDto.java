package ru.practicum.main.admin.compilation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {
    @NotNull
    @NotBlank
    @Length(min = 1, max = 50)
    private String title;

    private Collection<Integer> events;

    private Boolean pinned = false;
}
