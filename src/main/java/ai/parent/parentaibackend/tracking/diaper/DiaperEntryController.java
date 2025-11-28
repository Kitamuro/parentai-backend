package ai.parent.parentaibackend.tracking.diaper;

import ai.parent.parentaibackend.tracking.diaper.dto.CreateDiaperEntryRequest;
import ai.parent.parentaibackend.tracking.diaper.dto.DiaperEntryResponse;
import ai.parent.parentaibackend.tracking.diaper.dto.UpdateDiaperEntryRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/babies/{babyId}/diaper-entries")
@AllArgsConstructor
public class DiaperEntryController {

    private final DiaperEntryService diaperEntryService;

    @PostMapping
    public ResponseEntity<DiaperEntryResponse> createDiaperEntry(
            @PathVariable Long babyId,
            @Valid @RequestBody CreateDiaperEntryRequest request
    ) {
        DiaperEntry saved = diaperEntryService.createDiaperEntry(babyId, request);
        return ResponseEntity.ok(DiaperEntryMapper.toResponse(saved));
    }

    @GetMapping
    public List<DiaperEntryResponse> getDiaperEntries(
            @PathVariable Long babyId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return diaperEntryService.getDiaperEntries(babyId, from, to)
                .stream()
                .map(DiaperEntryMapper::toResponse)
                .toList();
    }

    @PatchMapping("/{entryId}")
    public ResponseEntity<DiaperEntryResponse> updateDiaperEntry(
            @PathVariable Long babyId,
            @PathVariable Long entryId,
            @RequestBody UpdateDiaperEntryRequest request
    ) {
        DiaperEntry updated = diaperEntryService.updateDiaperEntry(babyId, entryId, request);
        return ResponseEntity.ok(DiaperEntryMapper.toResponse(updated));
    }

    @DeleteMapping("/{entryId}")
    public ResponseEntity<Void> deleteDiaperEntry(
            @PathVariable Long babyId,
            @PathVariable Long entryId
    ) {
        diaperEntryService.deleteDiaperEntry(babyId, entryId);
        return ResponseEntity.noContent().build();
    }
}