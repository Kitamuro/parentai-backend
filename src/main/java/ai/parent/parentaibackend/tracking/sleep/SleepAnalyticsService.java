package ai.parent.parentaibackend.tracking.sleep;

import ai.parent.parentaibackend.baby.Baby;
import ai.parent.parentaibackend.baby.BabyRepository;
import ai.parent.parentaibackend.common.exception.ResourceNotFoundException;
import ai.parent.parentaibackend.tracking.sleep.dto.SleepSummaryResponse;
import ai.parent.parentaibackend.user.CurrentUserService;
import ai.parent.parentaibackend.user.User;
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
    private final CurrentUserService currentUserService;

    public SleepSummaryResponse getDailySummary(Long babyId, LocalDate date) {
        User currentUser = currentUserService.getCurrentUserOrThrow();

        Baby baby = babyRepository.findByIdAndUser(babyId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ребёнок с id=" + babyId + " не найден или недоступен для текущего пользователя"
                ));

        LocalDateTime from = date.atStartOfDay();
        LocalDateTime to = date.plusDays(1).atStartOfDay().minusNanos(1);

        List<SleepEvent> events = sleepEventRepository
                .findByBabyAndStartTimeBetweenOrderByStartTimeAsc(baby, from, to);

        long totalMinutes = 0;
        long dayMinutes = 0;
        long nightMinutes = 0;

        for (SleepEvent event : events) {
            if (event.getEndTime() == null) continue;

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
