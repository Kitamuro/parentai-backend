package ai.parent.parentaibackend.tracking.feeding;

import ai.parent.parentaibackend.common.exception.ResourceNotFoundException;
import ai.parent.parentaibackend.tracking.feeding.dto.CreateFeedingEventRequest;
import ai.parent.parentaibackend.tracking.feeding.dto.FeedingSummaryResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/babies/{babyId}/feeding-events")
public class FeedingEventController {

    private final FeedingEventService feedingEventService;
    private final FeedingAnalyticsService feedingAnalyticsService;

    public FeedingEventController(FeedingEventService feedingEventService,
                                  FeedingAnalyticsService feedingAnalyticsService) {
        this.feedingEventService = feedingEventService;
        this.feedingAnalyticsService = feedingAnalyticsService;
    }

    @PostMapping
    public ResponseEntity<?> createFeedingEvent(
            @PathVariable Long babyId,
            @RequestBody CreateFeedingEventRequest request
    ) {
        try {
            FeedingEvent saved = feedingEventService.createFeedingEvent(babyId, request);
            return ResponseEntity.ok(saved);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getFeedingEvents(
            @PathVariable Long babyId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime to
    ) {
        try {
            List<FeedingEvent> events = feedingEventService.getFeedingEvents(babyId, from, to);
            return ResponseEntity.ok(events);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<?> getDailySummary(
            @PathVariable Long babyId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        try {
            FeedingSummaryResponse summary = feedingAnalyticsService.getDailySummary(babyId, date);
            if (summary == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Сводка кормлений для ребёнка с id=" + babyId + " не найдена");
            }
            return ResponseEntity.ok(summary);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
