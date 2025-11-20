package ai.parent.parentaibackend.status;

import ai.parent.parentaibackend.baby.Baby;
import ai.parent.parentaibackend.baby.BabyRepository;
import ai.parent.parentaibackend.tracking.diaper.DiaperAnalyticsService;
import ai.parent.parentaibackend.tracking.diaper.DiaperEntry;
import ai.parent.parentaibackend.tracking.diaper.DiaperEntryRepository;
import ai.parent.parentaibackend.tracking.diaper.dto.DiaperSummaryResponse;
import ai.parent.parentaibackend.tracking.feeding.FeedingAnalyticsService;
import ai.parent.parentaibackend.tracking.feeding.FeedingEvent;
import ai.parent.parentaibackend.tracking.feeding.FeedingEventRepository;
import ai.parent.parentaibackend.tracking.feeding.dto.FeedingSummaryResponse;
import ai.parent.parentaibackend.tracking.sleep.SleepAnalyticsService;
import ai.parent.parentaibackend.tracking.sleep.SleepEvent;
import ai.parent.parentaibackend.tracking.sleep.SleepEventRepository;
import ai.parent.parentaibackend.tracking.sleep.dto.SleepSummaryResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@AllArgsConstructor
public class TodayStatusService {
    private final BabyRepository babyRepository;
    private final SleepAnalyticsService sleepAnalyticsService;
    private final FeedingAnalyticsService feedingAnalyticsService;
    private final DiaperAnalyticsService diaperAnalyticsService;
    private final SleepEventRepository sleepEventRepository;
    private final FeedingEventRepository feedingEventRepository;
    private final DiaperEntryRepository diaperEntryRepository;

    public TodayStatusResponse getTodayStatus(Long babyId, LocalDate date) {
        Baby baby = babyRepository.findById(babyId).orElse(null);
        if (baby == null) {
            return null;
        }

        SleepSummaryResponse sleepSummary = sleepAnalyticsService.getDailySummary(babyId, date);
        FeedingSummaryResponse feedingSummary = feedingAnalyticsService.getDailySummary(babyId, date);
        DiaperSummaryResponse diaperSummary = diaperAnalyticsService.getDailySummary(babyId, date);

        SleepEvent lastSleep = sleepEventRepository
                .findFirstByBabyOrderByStartTimeDesc(baby)
                .orElse(null);

        FeedingEvent lastFeeding = feedingEventRepository
                .findFirstByBabyOrderByStartTimeDesc(baby)
                .orElse(null);

        DiaperEntry lastDiaper = diaperEntryRepository
                .findFirstByBabyOrderByTimeDesc(baby)
                .orElse(null);

        LastEventsDto lastEvents = new LastEventsDto(lastSleep, lastFeeding, lastDiaper);

        return new TodayStatusResponse(
                baby.getId(),
                date,
                sleepSummary,
                feedingSummary,
                diaperSummary,
                lastEvents
        );
    }
}
