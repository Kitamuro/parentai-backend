package ai.parent.parentaibackend.tracking.sleep.dto;

import ai.parent.parentaibackend.tracking.sleep.SleepType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class SleepEventResponse {

    private Long id;
    private Long babyId;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private SleepType type;
    private String notes;
}
