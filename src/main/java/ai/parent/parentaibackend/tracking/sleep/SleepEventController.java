package ai.parent.parentaibackend.tracking.sleep;


import ai.parent.parentaibackend.common.ResourceNotFoundException;
import ai.parent.parentaibackend.tracking.sleep.dto.CreateSleepEventRequest;
import ai.parent.parentaibackend.tracking.sleep.dto.SleepSummaryResponse;
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
    public ResponseEntity<?> createSleepEvent(
            @PathVariable Long babyId,
            @RequestBody CreateSleepEventRequest request
    ) {
        try {
            SleepEvent saved = sleepEventService.createSleepEvent(babyId, request);
            return ResponseEntity.ok(saved);
        } catch (ResourceNotFoundException e) {
            // ребёнок не найден или не принадлежит этому юзеру
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            // ошибка валидации входных данных
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Получить список событий сна (все или за интервал)
    @GetMapping
    public ResponseEntity<?> getSleepEvents(
            @PathVariable Long babyId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime to
    ) {
        try {
            List<SleepEvent> events = sleepEventService.getSleepEvents(babyId, from, to);
            return ResponseEntity.ok(events);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Сводка сна за день
    @GetMapping("/summary")
    public ResponseEntity<?> getDailySummary(
            @PathVariable Long babyId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        try {
            SleepSummaryResponse summary = sleepAnalyticsService.getDailySummary(babyId, date);
            if (summary == null) {
                // если внутри AnalyticsService нет такой проверки — можно вызывать туда currentUser в будущем
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Сводка сна для ребёнка с id=" + babyId + " не найдена");
            }
            return ResponseEntity.ok(summary);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
