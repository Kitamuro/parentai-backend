package ai.parent.parentaibackend.tracking.diaper;

import ai.parent.parentaibackend.baby.Baby;
import ai.parent.parentaibackend.baby.BabyRepository;
import ai.parent.parentaibackend.common.exception.ResourceNotFoundException;
import ai.parent.parentaibackend.tracking.diaper.dto.CreateDiaperEntryRequest;
import ai.parent.parentaibackend.tracking.diaper.dto.UpdateDiaperEntryRequest;
import ai.parent.parentaibackend.user.CurrentUserService;
import ai.parent.parentaibackend.user.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DiaperEntryService {

    private final BabyRepository babyRepository;
    private final DiaperEntryRepository diaperEntryRepository;
    private final CurrentUserService currentUserService;

    public DiaperEntryService(BabyRepository babyRepository,
                              DiaperEntryRepository diaperEntryRepository,
                              CurrentUserService currentUserService) {
        this.babyRepository = babyRepository;
        this.diaperEntryRepository = diaperEntryRepository;
        this.currentUserService = currentUserService;
    }

    public DiaperEntry createDiaperEntry(Long babyId, CreateDiaperEntryRequest request) {
        User currentUser = currentUserService.getCurrentUserOrThrow();

        Baby baby = babyRepository.findByIdAndUser(babyId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ребёнок с id=" + babyId + " не найден или недоступен для текущего пользователя"
                ));

        if (request.getTime() == null) {
            throw new IllegalArgumentException("Поле 'time' обязательно");
        }
        if (request.getType() == null) {
            throw new IllegalArgumentException("Поле 'type' обязательно (WET/STOOL/MIXED)");
        }

        DiaperEntry entry = new DiaperEntry();
        entry.setBaby(baby);
        entry.setTime(request.getTime());
        entry.setType(request.getType());
        entry.setNotes(request.getNotes());

        return diaperEntryRepository.save(entry);
    }

    public List<DiaperEntry> getDiaperEntries(Long babyId,
                                              LocalDateTime from,
                                              LocalDateTime to) {
        User currentUser = currentUserService.getCurrentUserOrThrow();

        Baby baby = babyRepository.findByIdAndUser(babyId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ребёнок с id=" + babyId + " не найден или недоступен для текущего пользователя"
                ));

        if (from != null && to != null) {
            return diaperEntryRepository
                    .findByBabyAndTimeBetweenOrderByTimeAsc(baby, from, to);
        } else {
            return diaperEntryRepository
                    .findByBabyOrderByTimeAsc(baby);
        }
    }

    public DiaperEntry updateDiaperEntry(Long babyId, Long entryId, UpdateDiaperEntryRequest request) {
        User currentUser = currentUserService.getCurrentUserOrThrow();

        Baby baby = babyRepository.findByIdAndUser(babyId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ребёнок с id=" + babyId + " не найден у текущего пользователя"
                ));

        DiaperEntry entry = diaperEntryRepository.findById(entryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Запись по подгузнику с id=" + entryId + " не найдена"
                ));

        if (!entry.getBaby().getId().equals(baby.getId())) {
            throw new ResourceNotFoundException(
                    "Запись по подгузнику с id=" + entryId + " не принадлежит ребёнку с id=" + babyId
            );
        }

        if (request.getTime() != null) {
            entry.setTime(request.getTime());
        }
        if (request.getType() != null) {
            entry.setType(request.getType());
        }
        if (request.getNotes() != null) {
            entry.setNotes(request.getNotes());
        }

        return diaperEntryRepository.save(entry);
    }

    public void deleteDiaperEntry(Long babyId, Long entryId) {
        User currentUser = currentUserService.getCurrentUserOrThrow();

        Baby baby = babyRepository.findByIdAndUser(babyId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ребёнок с id=" + babyId + " не найден у текущего пользователя"
                ));

        DiaperEntry entry = diaperEntryRepository.findById(entryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Запись по подгузнику с id=" + entryId + " не найдена"
                ));

        if (!entry.getBaby().getId().equals(baby.getId())) {
            throw new ResourceNotFoundException(
                    "Запись по подгузнику с id=" + entryId + " не принадлежит ребёнку с id=" + babyId
            );
        }

        diaperEntryRepository.delete(entry);
    }
}
