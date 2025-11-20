package ai.parent.parentaibackend.tracking.sleep.dto;

import ai.parent.parentaibackend.tracking.sleep.SleepType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateSleepEventRequest {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private SleepType type;
    private String notes;
}
