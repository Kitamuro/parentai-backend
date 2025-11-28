package ai.parent.parentaibackend.baby.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateBabyRequest {

    @NotBlank(message = "Имя ребёнка обязательно")
    @Size(max = 100, message = "Имя слишком длинное")
    private String name;

    // Можно оставить необязательным
    private LocalDate dateOfBirth;

    // Если gender — String, можно ограничить длину
    @Size(max = 20, message = "Некорректный формат пола")
    private String gender;

    @Size(max = 500, message = "Слишком длинная заметка")
    private String notes;
}
