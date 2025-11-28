package ai.parent.parentaibackend.tracking.feeding;

import ai.parent.parentaibackend.common.exception.ResourceNotFoundException;
import ai.parent.parentaibackend.tracking.feeding.dto.CreateFeedingEventRequest;
import ai.parent.parentaibackend.tracking.feeding.dto.FeedingEventResponse;
import ai.parent.parentaibackend.tracking.feeding.dto.FeedingSummaryResponse;
import ai.parent.parentaibackend.tracking.feeding.dto.UpdateFeedingEventRequest;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
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
    public ResponseEntity<FeedingEventResponse> createFeedingEvent(
            @PathVariable Long babyId,
            @Valid @RequestBody CreateFeedingEventRequest request
    ) {
        FeedingEvent saved = feedingEventService.createFeedingEvent(babyId, request);
        return ResponseEntity.ok(FeedingEventMapper.toResponse(saved));
    }

    @GetMapping
    public List<FeedingEventResponse> getFeedingEvents(
            @PathVariable Long babyId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return feedingEventService.getFeedingEvents(babyId, from, to)
                .stream()
                .map(FeedingEventMapper::toResponse)
                .toList();
    }

    @GetMapping("/summary")
    public ResponseEntity<FeedingSummaryResponse> getDailySummary(
            @PathVariable Long babyId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        FeedingSummaryResponse summary = feedingAnalyticsService.getDailySummary(babyId, date);
        if (summary == null) {
            // Отдаём 404 — дальше GlobalExceptionHandler всё красиво обернёт
            throw new ResourceNotFoundException(
                    "Сводка сна для ребёнка с id=" + babyId + " не найдена"
            );
        }
        return ResponseEntity.ok(summary);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<FeedingEventResponse> updateFeedingEvent(
            @PathVariable Long babyId,
            @PathVariable Long eventId,
            @RequestBody UpdateFeedingEventRequest request
    ) {
        FeedingEvent updated = feedingEventService.updateFeedingEvent(babyId, eventId, request);
        return ResponseEntity.ok(FeedingEventMapper.toResponse(updated));
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteFeedingEvent(
            @PathVariable Long babyId,
            @PathVariable Long eventId
    ) {
        feedingEventService.deleteFeedingEvent(babyId, eventId);
        return ResponseEntity.noContent().build();
    }
}
