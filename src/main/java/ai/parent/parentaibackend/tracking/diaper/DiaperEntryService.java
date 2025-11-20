package ai.parent.parentaibackend.tracking.diaper;

import ai.parent.parentaibackend.baby.Baby;
import ai.parent.parentaibackend.baby.BabyRepository;
import ai.parent.parentaibackend.tracking.diaper.dto.CreateDiaperEntryRequest;
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

    public DiaperEntry createDiaperEntry(Long babyId, CreateDiaperEntryRequest request) {
        Optional<Baby> babyOpt = babyRepository.findById(babyId);
        if (babyOpt.isEmpty()) {
            return null; // baby не найден
        }

        if (request.getTime() == null || request.getType() == null) {
            throw new IllegalArgumentException("time and type are required");
        }

        Baby baby = babyOpt.get();

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