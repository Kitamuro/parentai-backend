package ai.parent.parentaibackend.tracking.feeding;

import ai.parent.parentaibackend.baby.Baby;
import ai.parent.parentaibackend.baby.BabyRepository;
import ai.parent.parentaibackend.common.ResourceNotFoundException;
import ai.parent.parentaibackend.tracking.feeding.dto.CreateFeedingEventRequest;
import ai.parent.parentaibackend.user.CurrentUserService;
import ai.parent.parentaibackend.user.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class FeedingEventService {

    private final BabyRepository babyRepository;
    private final FeedingEventRepository feedingEventRepository;
    private final CurrentUserService currentUserService;

    public FeedingEvent createFeedingEvent(Long babyId, CreateFeedingEventRequest request) {
        User currentUser = currentUserService.getCurrentUserOrThrow();

        Baby baby = babyRepository.findByIdAndUser(babyId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ребёнок с id=" + babyId + " не найден или недоступен для текущего пользователя"
                ));

        if (request.getStartTime() == null) {
            throw new IllegalArgumentException("Поле 'startTime' обязательно");
        }
        if (request.getType() == null) {
            throw new IllegalArgumentException("Поле 'type' обязательно (BREAST_LEFT/BREAST_RIGHT/FORMULA/SOLID)");
        }

        FeedingEvent event = new FeedingEvent();
        event.setBaby(baby);
        event.setStartTime(request.getStartTime());
        event.setEndTime(request.getEndTime());
        event.setType(request.getType());
        event.setVolumeMl(request.getVolumeMl());
        event.setNotes(request.getNotes());

        return feedingEventRepository.save(event);
    }

    public List<FeedingEvent> getFeedingEvents(Long babyId,
                                               LocalDateTime from,
                                               LocalDateTime to) {
        Optional<Baby> babyOpt = babyRepository.findById(babyId);
        if (babyOpt.isEmpty()) {
            return null; // контроллер поймёт как 404
        }

        Baby baby = babyOpt.get();

        if (from != null && to != null) {
            return feedingEventRepository
                    .findByBabyAndStartTimeBetweenOrderByStartTimeAsc(baby, from, to);
        } else {
            return feedingEventRepository
                    .findByBabyOrderByStartTimeAsc(baby);
        }
    }
}