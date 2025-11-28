package ai.parent.parentaibackend.tracking.diaper.dto;

import ai.parent.parentaibackend.tracking.diaper.DiaperType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class DiaperEntryResponse {

    private Long id;
    private Long babyId;

    private LocalDateTime time;

    private DiaperType type;
    private String notes;
}
