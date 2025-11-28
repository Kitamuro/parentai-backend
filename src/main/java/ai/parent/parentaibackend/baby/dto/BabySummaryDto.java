package ai.parent.parentaibackend.baby.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BabySummaryDto {
    private Long id;
    private String name;
    private LocalDate dateOfBirth;
    private String gender;
    private String notes;
}
