package ai.parent.parentaibackend.baby.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class BabyResponse {
    private Long id;
    private String name;
    private LocalDate dateOfBirth;
    private String gender;
    private String notes;
}
