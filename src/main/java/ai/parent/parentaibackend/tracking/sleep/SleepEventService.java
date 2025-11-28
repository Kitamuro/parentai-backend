package ai.parent.parentaibackend.tracking.sleep;

import ai.parent.parentaibackend.baby.Baby;
import ai.parent.parentaibackend.baby.BabyRepository;
import ai.parent.parentaibackend.common.exception.ResourceNotFoundException;
import ai.parent.parentaibackend.tracking.sleep.dto.CreateSleepEventRequest;
import ai.parent.parentaibackend.tracking.sleep.dto.UpdateSleepEventRequest;
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

    public SleepEvent updateSleepEvent(Long babyId, Long eventId, UpdateSleepEventRequest request) {
        User currentUser = currentUserService.getCurrentUserOrThrow();

        Baby baby = babyRepository.findByIdAndUser(babyId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ребёнок с id=" + babyId + " не найден у текущего пользователя"
                ));

        SleepEvent event = sleepEventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Событие сна с id=" + eventId + " не найдено"
                ));

        // Дополнительная защита: событие должно принадлежать этому ребёнку
        if (!event.getBaby().getId().equals(baby.getId())) {
            throw new ResourceNotFoundException(
                    "Событие сна с id=" + eventId + " не принадлежит ребёнку с id=" + babyId
            );
        }

        if (request.getStartTime() != null) {
            event.setStartTime(request.getStartTime());
        }
        if (request.getEndTime() != null) {
            event.setEndTime(request.getEndTime());
        }
        if (request.getType() != null) {
            event.setType(request.getType());
        }
        if (request.getNotes() != null) {
            event.setNotes(request.getNotes());
        }

        return sleepEventRepository.save(event);
    }

    public void deleteSleepEvent(Long babyId, Long eventId) {
        User currentUser = currentUserService.getCurrentUserOrThrow();

        Baby baby = babyRepository.findByIdAndUser(babyId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ребёнок с id=" + babyId + " не найден у текущего пользователя"
                ));

        SleepEvent event = sleepEventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Событие сна с id=" + eventId + " не найдено"
                ));

        if (!event.getBaby().getId().equals(baby.getId())) {
            throw new ResourceNotFoundException(
                    "Событие сна с id=" + eventId + " не принадлежит ребёнку с id=" + babyId
            );
        }

        sleepEventRepository.delete(event);
    }
}
