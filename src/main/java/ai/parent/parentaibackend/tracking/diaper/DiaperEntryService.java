package ai.parent.parentaibackend.tracking.diaper;

import ai.parent.parentaibackend.baby.Baby;
import ai.parent.parentaibackend.baby.BabyRepository;
import ai.parent.parentaibackend.common.ResourceNotFoundException;
import ai.parent.parentaibackend.tracking.diaper.dto.CreateDiaperEntryRequest;
import ai.parent.parentaibackend.user.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class DiaperEntryService {

    private final BabyRepository babyRepository;
    private final DiaperEntryRepository diaperEntryRepository;
    private final CurrentUserService currentUserService;

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
        Optional<Baby> babyOpt = babyRepository.findById(babyId);
        if (babyOpt.isEmpty()) {
            return null;
        }

        Baby baby = babyOpt.get();

        if (from != null && to != null) {
            return diaperEntryRepository
                    .findByBabyAndTimeBetweenOrderByTimeAsc(baby, from, to);
        } else {
            return diaperEntryRepository
                    .findByBabyOrderByTimeAsc(baby);
        }
    }
}