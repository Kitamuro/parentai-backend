package ai.parent.parentaibackend.tracking.feeding;

import ai.parent.parentaibackend.baby.Baby;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FeedingEventRepository extends JpaRepository<FeedingEvent, Long> {

    List<FeedingEvent> findByBabyOrderByStartTimeAsc(Baby baby);

    List<FeedingEvent> findByBabyAndStartTimeBetweenOrderByStartTimeAsc(
            Baby baby,
            LocalDateTime from,
            LocalDateTime to
    );

    // последнее кормление
    Optional<FeedingEvent> findFirstByBabyOrderByStartTimeDesc(Baby baby);
}