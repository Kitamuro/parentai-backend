package ai.parent.parentaibackend.tracking.feeding;

import ai.parent.parentaibackend.baby.Baby;
import ai.parent.parentaibackend.baby.BabyRepository;
import ai.parent.parentaibackend.tracking.feeding.dto.CreateFeedingEventRequest;
import ai.parent.parentaibackend.tracking.feeding.dto.FeedingSummaryResponse;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/babies/{babyId}/feeding-events")
@AllArgsConstructor
public class FeedingEventController {

    private final FeedingEventService feedingEventService;
    private final FeedingAnalyticsService feedingAnalyticsService;

    // Создать событие кормления
    @PostMapping
    public ResponseEntity<?> createFeedingEvent(
            @PathVariable Long babyId,
            @RequestBody CreateFeedingEventRequest request
    ) {
        try {
            FeedingEvent saved = feedingEventService.createFeedingEvent(babyId, request);
            if (saved == null) {
                return ResponseEntity.notFound().build(); // baby не найден
            }
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Получить события кормлений (все или за интервал)
    @GetMapping
    public ResponseEntity<List<FeedingEvent>> getFeedingEvents(
            @PathVariable Long babyId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime to
    ) {
        List<FeedingEvent> events = feedingEventService.getFeedingEvents(babyId, from, to);
        if (events == null) {
            return ResponseEntity.notFound().build(); // baby не найден
        }
        return ResponseEntity.ok(events);
    }

    // Сводка кормлений за день
    @GetMapping("/summary")
    public ResponseEntity<FeedingSummaryResponse> getDailySummary(
            @PathVariable Long babyId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        FeedingSummaryResponse summary = feedingAnalyticsService.getDailySummary(babyId, date);
        if (summary == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(summary);
    }
}