package ai.parent.parentaibackend.baby.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Data
public class CreateBabyRequest {
    private String name;
    private LocalDate dateOfBirth;
    private String gender;
    private String notes;
}
