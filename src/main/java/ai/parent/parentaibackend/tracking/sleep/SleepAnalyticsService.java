package ai.parent.parentaibackend.tracking.sleep;

import ai.parent.parentaibackend.baby.Baby;
import ai.parent.parentaibackend.baby.BabyRepository;
import ai.parent.parentaibackend.tracking.sleep.dto.SleepSummaryResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class SleepAnalyticsService {
    private final BabyRepository babyRepository;
    private final SleepEventRepository sleepEventRepository;

    public SleepSummaryResponse getDailySummary(Long babyId, LocalDate date) {
        Baby baby = babyRepository.findById(babyId).orElse(null);
        if (baby == null) {
            return null;
        }

        LocalDateTime from = date.atStartOfDay();
        LocalDateTime to = date.plusDays(1).atStartOfDay().minusNanos(1);

        List<SleepEvent> events = sleepEventRepository
                .findByBabyAndStartTimeBetweenOrderByStartTimeAsc(baby, from, to);

        long totalMinutes = 0;
        long dayMinutes = 0;
        long nightMinutes = 0;

        for (SleepEvent event : events) {
            // если сон ещё не завершён (endTime == null), пока игнорируем
            if (event.getEndTime() == null) {
                continue;
            }

            long minutes = Duration.between(event.getStartTime(), event.getEndTime()).toMinutes();

            totalMinutes += minutes;

            if (event.getType() == SleepType.DAY) {
                dayMinutes += minutes;
            } else if (event.getType() == SleepType.NIGHT) {
                nightMinutes += minutes;
            }
        }

        return new SleepSummaryResponse(
                baby.getId(),
                date,
                totalMinutes,
                dayMinutes,
                nightMinutes,
                events.size()
        );
    }
}
