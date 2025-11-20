package ai.parent.parentaibackend.status;

import ai.parent.parentaibackend.tracking.diaper.dto.DiaperSummaryResponse;
import ai.parent.parentaibackend.tracking.feeding.dto.FeedingSummaryResponse;
import ai.parent.parentaibackend.tracking.sleep.dto.SleepSummaryResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class TodayStatusResponse {
    private Long babyId;
    private LocalDate date;
    private SleepSummaryResponse sleepSummary;
    private FeedingSummaryResponse feedingSummary;
    private DiaperSummaryResponse diaperSummary;
    private LastEventsDto lastEvents;
}
