package ai.parent.parentaibackend.tracking.diaper.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class DiaperSummaryResponse {
    private Long babyId;
    private LocalDate date;
    private int totalDiapers;
    private int wet;
    private int stool;
    private int mixed;
}
