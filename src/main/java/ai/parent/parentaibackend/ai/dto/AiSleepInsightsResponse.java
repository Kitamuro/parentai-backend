package ai.parent.parentaibackend.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Текстовые “инсайты” по сну + немного сырой статистики,
 * чтобы мобилка могла что-то показывать в UI.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiSleepInsightsResponse {

    private Long babyId;
    private String babyName;
    private String babyAge;

    /**
     * Период анализа (обычно 2–3 дня).
     */
    private LocalDate fromDate;
    private LocalDate toDate;

    /**
     * Общее время сна (минуты) за период.
     */
    private long totalSleepMinutes;

    /**
     * Среднее количество сна в сутки за период.
     */
    private long avgSleepMinutesPerDay;

    /**
     * Общее количество эпизодов сна.
     */
    private int totalSleepEvents;

    /**
     * Основной текстовый вывод “ассистента”.
     */
    private String summaryText;

    /**
     * Дополнительные короткие подсказки (можно показать списком).
     */
    private List<String> hints;
}
