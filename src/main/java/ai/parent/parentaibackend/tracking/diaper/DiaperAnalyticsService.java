package ai.parent.parentaibackend.tracking.diaper;

import ai.parent.parentaibackend.baby.Baby;
import ai.parent.parentaibackend.baby.BabyRepository;
import ai.parent.parentaibackend.tracking.diaper.dto.DiaperSummaryResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class DiaperAnalyticsService {

    private final BabyRepository babyRepository;
    private final DiaperEntryRepository diaperEntryRepository;

    public DiaperSummaryResponse getDailySummary(Long babyId, LocalDate date) {
        Baby baby = babyRepository.findById(babyId).orElse(null);
        if (baby == null) {
            return null;
        }

        LocalDateTime from = date.atStartOfDay();
        LocalDateTime to = date.plusDays(1).atStartOfDay().minusNanos(1);

        List<DiaperEntry> entries = diaperEntryRepository
                .findByBabyAndTimeBetweenOrderByTimeAsc(baby, from, to);

        int total = entries.size();
        int wet = 0;
        int stool = 0;
        int mixed = 0;

        for (DiaperEntry entry : entries) {
            switch (entry.getType()) {
                case WET -> wet++;
                case STOOL -> stool++;
                case MIXED -> mixed++;
            }
        }

        return new DiaperSummaryResponse(
                baby.getId(),
                date,
                total,
                wet,
                stool,
                mixed
        );
    }
}
