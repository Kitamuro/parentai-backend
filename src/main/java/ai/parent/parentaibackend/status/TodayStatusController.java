package ai.parent.parentaibackend.status;

import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@AllArgsConstructor
@RequestMapping("/api/babies/{babyId}/today")
public class TodayStatusController {
    private final TodayStatusService todayStatusService;

    @GetMapping
    public ResponseEntity<TodayStatusResponse> getTodayStatus(
            @PathVariable Long babyId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now();

        TodayStatusResponse status = todayStatusService.getTodayStatus(babyId, targetDate);
        if (status == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(status);
    }
}
