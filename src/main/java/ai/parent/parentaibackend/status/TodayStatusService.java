package ai.parent.parentaibackend.status;

import ai.parent.parentaibackend.baby.Baby;
import ai.parent.parentaibackend.baby.BabyRepository;
import ai.parent.parentaibackend.common.exception.ResourceNotFoundException;
import ai.parent.parentaibackend.tracking.diaper.DiaperEntry;
import ai.parent.parentaibackend.tracking.diaper.DiaperEntryRepository;
import ai.parent.parentaibackend.tracking.diaper.dto.DiaperSummaryResponse;
import ai.parent.parentaibackend.tracking.feeding.dto.FeedingSummaryResponse;
import ai.parent.parentaibackend.tracking.sleep.dto.SleepSummaryResponse;
import ai.parent.parentaibackend.tracking.feeding.FeedingEvent;
import ai.parent.parentaibackend.tracking.feeding.FeedingEventRepository;
import ai.parent.parentaibackend.tracking.sleep.SleepEvent;
import ai.parent.parentaibackend.tracking.sleep.SleepEventRepository;
import ai.parent.parentaibackend.tracking.sleep.SleepAnalyticsService;
import ai.parent.parentaibackend.tracking.feeding.FeedingAnalyticsService;
import ai.parent.parentaibackend.tracking.diaper.DiaperAnalyticsService;
import ai.parent.parentaibackend.user.CurrentUserService;
import ai.parent.parentaibackend.user.User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TodayStatusService {

    private final BabyRepository babyRepository;
    private final SleepAnalyticsService sleepAnalyticsService;
    private final FeedingAnalyticsService feedingAnalyticsService;
    private final DiaperAnalyticsService diaperAnalyticsService;
    private final SleepEventRepository sleepEventRepository;
    private final FeedingEventRepository feedingEventRepository;
    private final DiaperEntryRepository diaperEntryRepository;
    private final CurrentUserService currentUserService;

    public TodayStatusService(BabyRepository babyRepository,
                              SleepAnalyticsService sleepAnalyticsService,
                              FeedingAnalyticsService feedingAnalyticsService,
                              DiaperAnalyticsService diaperAnalyticsService,
                              SleepEventRepository sleepEventRepository,
                              FeedingEventRepository feedingEventRepository,
                              DiaperEntryRepository diaperEntryRepository,
                              CurrentUserService currentUserService) {
        this.babyRepository = babyRepository;
        this.sleepAnalyticsService = sleepAnalyticsService;
        this.feedingAnalyticsService = feedingAnalyticsService;
        this.diaperAnalyticsService = diaperAnalyticsService;
        this.sleepEventRepository = sleepEventRepository;
        this.feedingEventRepository = feedingEventRepository;
        this.diaperEntryRepository = diaperEntryRepository;
        this.currentUserService = currentUserService;
    }

    public TodayStatusResponse getTodayStatus(Long babyId, LocalDate date) {
        User currentUser = currentUserService.getCurrentUserOrThrow();

        Baby baby = babyRepository.findByIdAndUser(babyId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ребёнок с id=" + babyId + " не найден или недоступен для текущего пользователя"
                ));

        SleepSummaryResponse sleepSummary = sleepAnalyticsService.getDailySummary(baby.getId(), date);
        FeedingSummaryResponse feedingSummary = feedingAnalyticsService.getDailySummary(baby.getId(), date);
        DiaperSummaryResponse diaperSummary = diaperAnalyticsService.getDailySummary(baby.getId(), date);

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
