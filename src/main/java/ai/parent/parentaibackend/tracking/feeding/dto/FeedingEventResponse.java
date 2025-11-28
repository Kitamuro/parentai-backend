package ai.parent.parentaibackend.tracking.feeding.dto;

import ai.parent.parentaibackend.tracking.feeding.FeedingType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class FeedingEventResponse {

    private Long id;
    private Long babyId;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private FeedingType type;

    /**
     * Объём (если есть) — в мл.
     */
    private Integer volumeMl;

    private String notes;
}
