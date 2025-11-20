package ai.parent.parentaibackend.tracking.sleep;

import ai.parent.parentaibackend.baby.Baby;
import ai.parent.parentaibackend.baby.BabyRepository;
import ai.parent.parentaibackend.tracking.sleep.dto.CreateSleepEventRequest;
import ai.parent.parentaibackend.tracking.sleep.dto.SleepSummaryResponse;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/babies/{babyId}/sleep-events")
public class SleepEventController {

    private final BabyRepository babyRepository;
    private final SleepEventRepository sleepEventRepository;
    private final SleepAnalyticsService sleepAnalyticsService;

    @PostMapping
    public ResponseEntity<SleepEvent> createSleepEvent(@PathVariable Long babyId,
                                                       @RequestBody CreateSleepEventRequest request) {

        Baby baby = babyRepository.findById(babyId)
                .orElse(null);

        if (baby == null) {
            return ResponseEntity.notFound().build();
        }

        if (request.getStartTime() == null || request.getType() == null) {
            return ResponseEntity.badRequest().build();
        }

        SleepEvent event = SleepEvent.builder()
                .baby(baby)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .type(request.getType())
                .notes(request.getNotes()).build();

        SleepEvent saved = sleepEventRepository.save(event);
        return ResponseEntity.ok(saved);
    }

    // Получить все события сна ребёнка (можно с интервалом по времени)
    @GetMapping
    public ResponseEntity<List<SleepEvent>> getSleepEvents(
            @PathVariable Long babyId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime to
    ) {
        Baby baby = babyRepository.findById(babyId)
                .orElse(null);

        if (baby == null) {
            return ResponseEntity.notFound().build();
        }

        List<SleepEvent> events;
        if (from != null && to != null) {
            events = sleepEventRepository
                    .findByBabyAndStartTimeBetweenOrderByStartTimeAsc(baby, from, to);
        } else {
            events = sleepEventRepository
                    .findByBabyOrderByStartTimeAsc(baby);
        }

        return ResponseEntity.ok(events);
    }

    @GetMapping("/summary")
    public ResponseEntity<SleepSummaryResponse> getDailySummary(
            @PathVariable Long babyId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        SleepSummaryResponse summary = sleepAnalyticsService.getDailySummary(babyId, date);
        if (summary == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(summary);
    }
}