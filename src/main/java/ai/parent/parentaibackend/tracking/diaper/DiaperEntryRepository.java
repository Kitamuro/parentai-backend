package ai.parent.parentaibackend.tracking.diaper;

import ai.parent.parentaibackend.baby.Baby;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DiaperEntryRepository extends JpaRepository<DiaperEntry, Long> {

    List<DiaperEntry> findByBabyOrderByTimeAsc(Baby baby);

    List<DiaperEntry> findByBabyAndTimeBetweenOrderByTimeAsc(
            Baby baby,
            LocalDateTime from,
            LocalDateTime to
    );

    // новый метод — последний подгузник
    Optional<DiaperEntry> findFirstByBabyOrderByTimeDesc(Baby baby);
}
