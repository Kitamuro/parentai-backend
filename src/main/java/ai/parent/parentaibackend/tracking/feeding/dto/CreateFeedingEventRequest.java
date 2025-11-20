package ai.parent.parentaibackend.tracking.feeding.dto;

import ai.parent.parentaibackend.tracking.feeding.FeedingType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateFeedingEventRequest {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private FeedingType type;
    private Integer volumeMl;
    private String notes;
}
