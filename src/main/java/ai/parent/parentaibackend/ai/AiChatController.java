package ai.parent.parentaibackend.ai;

import ai.parent.parentaibackend.ai.dto.AiChatHistoryResponse;
import ai.parent.parentaibackend.ai.dto.AiChatRequest;
import ai.parent.parentaibackend.ai.dto.AiChatResponse;
import ai.parent.parentaibackend.ai.dto.AiSleepInsightsResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/ai")
@AllArgsConstructor
public class AiChatController {

    private final AiChatService aiChatService;
    private final AiSleepInsightsService aiSleepInsightsService;

    /**
     * Простейший AI-чат.
     * Авторизация обязательна (по SecurityConfig).
     *
     * POST /api/ai/chat
     *
     * Request:
     * {
     *   "message": "Почему ребёнок плохо спит вечером?",
     *   "babyId": 1
     * }
     *
     * Response: AiChatResponse
     */
    @PostMapping("/chat")
    public ResponseEntity<AiChatResponse> chat(
            @Valid @RequestBody AiChatRequest request
    ) {
        AiChatResponse response = aiChatService.chat(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<AiChatHistoryResponse> getHistory(
            @RequestParam(required = false) Long babyId
    ) {
        AiChatHistoryResponse history = aiChatService.getHistory(babyId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/sleep-insights")
    public ResponseEntity<AiSleepInsightsResponse> getSleepInsights(
            @RequestParam Long babyId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        AiSleepInsightsResponse response = aiSleepInsightsService.getSleepInsights(babyId, date);
        return ResponseEntity.ok(response);
    }
}
