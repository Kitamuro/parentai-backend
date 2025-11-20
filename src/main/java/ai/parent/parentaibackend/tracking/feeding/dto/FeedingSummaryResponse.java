package ai.parent.parentaibackend.tracking.feeding.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class FeedingSummaryResponse {
    private Long babyId;
    private LocalDate date;
    private int totalFeedings;
    private int breastFeedings;
    private int formulaFeedings;
    private int solidFeedings;
    private int totalVolumeMl;
}
