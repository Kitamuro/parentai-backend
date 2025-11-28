package ai.parent.parentaibackend.baby.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * Частичное обновление ребёнка.
 * Все поля опциональны — меняем только то, что пришло в запросе.
 */
@Data
public class UpdateBabyRequest {

    private String name;
    private LocalDate dateOfBirth;
    private String gender;
    private String notes;
}
