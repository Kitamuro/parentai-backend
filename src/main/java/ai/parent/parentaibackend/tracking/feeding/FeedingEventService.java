package ai.parent.parentaibackend.tracking.feeding;

import ai.parent.parentaibackend.baby.Baby;
import ai.parent.parentaibackend.baby.BabyRepository;
import ai.parent.parentaibackend.common.exception.ResourceNotFoundException;
import ai.parent.parentaibackend.tracking.feeding.dto.CreateFeedingEventRequest;
import ai.parent.parentaibackend.tracking.feeding.dto.UpdateFeedingEventRequest;
import ai.parent.parentaibackend.user.CurrentUserService;
import ai.parent.parentaibackend.user.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FeedingEventService {

    private final BabyRepository babyRepository;
    private final FeedingEventRepository feedingEventRepository;
    private final CurrentUserService currentUserService;

    public FeedingEventService(BabyRepository babyRepository,
                               FeedingEventRepository feedingEventRepository,
                               CurrentUserService currentUserService) {
        this.babyRepository = babyRepository;
        this.feedingEventRepository = feedingEventRepository;
        this.currentUserService = currentUserService;
    }

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
            throw new IllegalArgumentException(
                    "Поле 'type' обязательно (BREAST_LEFT, BREAST_RIGHT, FORMULA, SOLID)"
            );
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
        User currentUser = currentUserService.getCurrentUserOrThrow();

        Baby baby = babyRepository.findByIdAndUser(babyId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ребёнок с id=" + babyId + " не найден или недоступен для текущего пользователя"
                ));

        if (from != null && to != null) {
            return feedingEventRepository
                    .findByBabyAndStartTimeBetweenOrderByStartTimeAsc(baby, from, to);
        } else {
            return feedingEventRepository
                    .findByBabyOrderByStartTimeAsc(baby);
        }
    }

    public FeedingEvent updateFeedingEvent(Long babyId, Long eventId, UpdateFeedingEventRequest request) {
        User currentUser = currentUserService.getCurrentUserOrThrow();

        Baby baby = babyRepository.findByIdAndUser(babyId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ребёнок с id=" + babyId + " не найден у текущего пользователя"
                ));

        FeedingEvent event = feedingEventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Событие кормления с id=" + eventId + " не найдено"
                ));

        if (!event.getBaby().getId().equals(baby.getId())) {
            throw new ResourceNotFoundException(
                    "Событие кормления с id=" + eventId + " не принадлежит ребёнку с id=" + babyId
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
        if (request.getVolumeMl() != null) {
            event.setVolumeMl(request.getVolumeMl());
        }
        if (request.getNotes() != null) {
            event.setNotes(request.getNotes());
        }

        return feedingEventRepository.save(event);
    }

    public void deleteFeedingEvent(Long babyId, Long eventId) {
        User currentUser = currentUserService.getCurrentUserOrThrow();

        Baby baby = babyRepository.findByIdAndUser(babyId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ребёнок с id=" + babyId + " не найден у текущего пользователя"
                ));

        FeedingEvent event = feedingEventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Событие кормления с id=" + eventId + " не найдено"
                ));

        if (!event.getBaby().getId().equals(baby.getId())) {
            throw new ResourceNotFoundException(
                    "Событие кормления с id=" + eventId + " не принадлежит ребёнку с id=" + babyId
            );
        }

        feedingEventRepository.delete(event);
    }
}
