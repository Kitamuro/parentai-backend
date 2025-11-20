package ai.parent.parentaibackend.tracking.feeding;

import ai.parent.parentaibackend.baby.Baby;
import ai.parent.parentaibackend.baby.BabyRepository;
import ai.parent.parentaibackend.tracking.feeding.dto.FeedingSummaryResponse;
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

    public FeedingSummaryResponse getDailySummary(Long babyId, LocalDate date) {
        Baby baby = babyRepository.findById(babyId).orElse(null);
        if (baby == null) {
            return null;
        }

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
            if (event.getType() == FeedingType.BREAST_LEFT ||
                    event.getType() == FeedingType.BREAST_RIGHT) {
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
