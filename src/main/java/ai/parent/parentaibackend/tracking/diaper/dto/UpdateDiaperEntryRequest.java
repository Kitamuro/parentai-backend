package ai.parent.parentaibackend.tracking.diaper.dto;

import ai.parent.parentaibackend.tracking.diaper.DiaperType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateDiaperEntryRequest {

    private LocalDateTime time;
    private DiaperType type;
    private String notes;
}
