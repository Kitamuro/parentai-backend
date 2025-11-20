package ai.parent.parentaibackend.tracking.sleep;

import ai.parent.parentaibackend.baby.Baby;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SleepEventRepository extends JpaRepository<SleepEvent, Long> {

    // все события сна для ребёнка
    List<SleepEvent> findByBabyOrderByStartTimeAsc(Baby baby);

    // события сна ребёнка за интервал
    List<SleepEvent> findByBabyAndStartTimeBetweenOrderByStartTimeAsc(
            Baby baby,
            LocalDateTime from,
            LocalDateTime to
    );

    // последний сон
    Optional<SleepEvent> findFirstByBabyOrderByStartTimeDesc(Baby baby);
}