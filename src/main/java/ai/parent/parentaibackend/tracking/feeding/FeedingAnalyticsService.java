package ai.parent.parentaibackend.tracking.feeding;

import ai.parent.parentaibackend.baby.Baby;
import ai.parent.parentaibackend.baby.BabyRepository;
import ai.parent.parentaibackend.common.exception.ResourceNotFoundException;
import ai.parent.parentaibackend.tracking.feeding.dto.FeedingSummaryResponse;
import ai.parent.parentaibackend.user.CurrentUserService;
import ai.parent.parentaibackend.user.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class FeedingAnalyticsService {

    private final BabyRepository babyRepository;
    private final FeedingEventRepository feedingEventRepository;
    private final CurrentUserService currentUserService;

    public FeedingSummaryResponse getDailySummary(Long babyId, LocalDate date) {
        User currentUser = currentUserService.getCurrentUserOrThrow();

        Baby baby = babyRepository.findByIdAndUser(babyId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ребёнок с id=" + babyId + " не найден или недоступен для текущего пользователя"
                ));

        LocalDateTime from = date.atStartOfDay();
        LocalDateTime to = date.plusDays(1).atStartOfDay().minusNanos(1);

        List<FeedingEvent> events = feedingEventRepository
                .findByBabyAndStartTimeBetweenOrderByStartTimeAsc(baby, from, to);

        int total = events.size();
        int breast = 0;
        int formula = 0;
        int solid = 0;
        int volumeTotal = 0;

        for (FeedingEvent event : events) {
            if (event.getType() == FeedingType.BREAST_LEFT
                    || event.getType() == FeedingType.BREAST_RIGHT) {
                breast++;
            } else if (event.getType() == FeedingType.FORMULA) {
                formula++;
            } else if (event.getType() == FeedingType.SOLID) {
                solid++;
            }

            if (event.getVolumeMl() != null) {
                volumeTotal += event.getVolumeMl();
            }
        }

        return new FeedingSummaryResponse(
                baby.getId(),
                date,
                total,
                breast,
                formula,
                solid,
                volumeTotal
        );
    }
}
