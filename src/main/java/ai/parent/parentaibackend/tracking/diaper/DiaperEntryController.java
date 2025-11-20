package ai.parent.parentaibackend.tracking.diaper;

import ai.parent.parentaibackend.common.ResourceNotFoundException;
import ai.parent.parentaibackend.tracking.diaper.dto.CreateDiaperEntryRequest;
import ai.parent.parentaibackend.tracking.feeding.FeedingEvent;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/babies/{babyId}/diaper-entries")
@AllArgsConstructor
public class DiaperEntryController {
    private final DiaperEntryService diaperEntryService;


    // Создать запись подгузника
    @PostMapping
    public ResponseEntity<?> createDiaperEntry(
            @PathVariable Long babyId,
            @RequestBody CreateDiaperEntryRequest request
    ) {
        try {
            DiaperEntry saved = diaperEntryService.createDiaperEntry(babyId, request);
            return ResponseEntity.ok(saved);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Получить список подгузников (все или за интервал)
    @GetMapping
    public ResponseEntity<List<DiaperEntry>> getDiaperEntries(
            @PathVariable Long babyId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime to
    ) {
        List<DiaperEntry> entries = diaperEntryService.getDiaperEntries(babyId, from, to);
        if (entries == null) {
            return ResponseEntity.notFound().build(); // baby не найден
        }
        return ResponseEntity.ok(entries);
    }
}