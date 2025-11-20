package ai.parent.parentaibackend.tracking.diaper.dto;

import ai.parent.parentaibackend.tracking.diaper.DiaperType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CreateDiaperEntryRequest {
    private LocalDateTime time;
    private DiaperType type;
    private String notes;
}
