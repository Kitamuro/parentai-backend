package ai.parent.parentaibackend.tracking.sleep.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class SleepSummaryResponse {
    private Long babyId;
    private LocalDate date;
    private long totalSleepMinutes;
    private long daySleepMinutes;
    private long nightSleepMinutes;
    private int sleepEventsCount;
}
