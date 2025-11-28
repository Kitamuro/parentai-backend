package ai.parent.parentaibackend.tracking.sleep.dto;

import ai.parent.parentaibackend.tracking.sleep.SleepType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Частичное обновление события сна.
 */
@Data
public class UpdateSleepEventRequest {

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private SleepType type;
    private String notes;
}
