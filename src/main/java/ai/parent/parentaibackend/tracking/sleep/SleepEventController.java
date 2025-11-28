package ai.parent.parentaibackend.tracking.sleep;


import ai.parent.parentaibackend.common.exception.ResourceNotFoundException;
import ai.parent.parentaibackend.tracking.sleep.dto.CreateSleepEventRequest;
import ai.parent.parentaibackend.tracking.sleep.dto.SleepEventResponse;
import ai.parent.parentaibackend.tracking.sleep.dto.SleepSummaryResponse;
import ai.parent.parentaibackend.tracking.sleep.dto.UpdateSleepEventRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/babies/{babyId}/sleep-events")
@AllArgsConstructor
public class SleepEventController {

    private final SleepEventService sleepEventService;
    private final SleepAnalyticsService sleepAnalyticsService;

    // Создать событие сна
    @PostMapping
    public ResponseEntity<SleepEventResponse> createSleepEvent(
            @PathVariable Long babyId,
            @Valid @RequestBody CreateSleepEventRequest request
    ) {
        SleepEvent saved = sleepEventService.createSleepEvent(babyId, request);
        return ResponseEntity.ok(SleepEventMapper.toResponse(saved));
    }

    // Получить список событий сна (все или за интервал)
    @GetMapping
    public List<SleepEventResponse> getSleepEvents(
            @PathVariable Long babyId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return sleepEventService.getSleepEvents(babyId, from, to)
                .stream()
                .map(SleepEventMapper::toResponse)
                .toList();
    }

    // Сводка сна за день
    @GetMapping("/summary")
    public ResponseEntity<SleepSummaryResponse> getDailySummary(
            @PathVariable Long babyId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        SleepSummaryResponse summary = sleepAnalyticsService.getDailySummary(babyId, date);

        if (summary == null) {
            // Отдаём 404 — дальше GlobalExceptionHandler всё красиво обернёт
            throw new ResourceNotFoundException(
                    "Сводка сна для ребёнка с id=" + babyId + " не найдена"
            );
        }
        return ResponseEntity.ok(summary);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<SleepEventResponse> updateSleepEvent(
            @PathVariable Long babyId,
            @PathVariable Long eventId,
            @RequestBody UpdateSleepEventRequest request
    ) {
        SleepEvent updated = sleepEventService.updateSleepEvent(babyId, eventId, request);
        return ResponseEntity.ok(SleepEventMapper.toResponse(updated));
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteSleepEvent(
            @PathVariable Long babyId,
            @PathVariable Long eventId
    ) {
        sleepEventService.deleteSleepEvent(babyId, eventId);
        return ResponseEntity.noContent().build();
    }
}
