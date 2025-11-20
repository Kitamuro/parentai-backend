package ai.parent.parentaibackend.tracking.sleep;

import ai.parent.parentaibackend.baby.Baby;
import ai.parent.parentaibackend.baby.BabyRepository;
import ai.parent.parentaibackend.common.ResourceNotFoundException;
import ai.parent.parentaibackend.tracking.sleep.dto.CreateSleepEventRequest;
import ai.parent.parentaibackend.user.CurrentUserService;
import ai.parent.parentaibackend.user.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class SleepEventService {

    private final BabyRepository babyRepository;
    private final SleepEventRepository sleepEventRepository;
    private final CurrentUserService currentUserService;

    public SleepEvent createSleepEvent(Long babyId, CreateSleepEventRequest request) {
        // текущий пользователь из JWT
        User currentUser = currentUserService.getCurrentUserOrThrow();

        // проверяем, что ребёнок принадлежит этому пользователю
        Baby baby = babyRepository.findByIdAndUser(babyId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ребёнок с id=" + babyId + " не найден или недоступен для текущего пользователя"
                ));

        // валидация входных данных
        if (request.getStartTime() == null) {
            throw new IllegalArgumentException("Поле 'startTime' обязательно");
        }
        if (request.getType() == null) {
            throw new IllegalArgumentException("Поле 'type' обязательно (DAY или NIGHT)");
        }

        SleepEvent event = new SleepEvent();
        event.setBaby(baby);
        event.setStartTime(request.getStartTime());
        event.setEndTime(request.getEndTime());
        event.setType(request.getType());
        event.setNotes(request.getNotes());

        return sleepEventRepository.save(event);
    }

    public List<SleepEvent> getSleepEvents(Long babyId,
                                           LocalDateTime from,
                                           LocalDateTime to) {
        User currentUser = currentUserService.getCurrentUserOrThrow();

        Baby baby = babyRepository.findByIdAndUser(babyId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ребёнок с id=" + babyId + " не найден или недоступен для текущего пользователя"
                ));

        if (from != null && to != null) {
            return sleepEventRepository
                    .findByBabyAndStartTimeBetweenOrderByStartTimeAsc(baby, from, to);
        } else {
            return sleepEventRepository
                    .findByBabyOrderByStartTimeAsc(baby);
        }
    }
}
