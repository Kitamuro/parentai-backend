package ai.parent.parentaibackend.tracking.feeding.dto;

import ai.parent.parentaibackend.tracking.feeding.FeedingType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateFeedingEventRequest {

    @NotNull(message = "Время начала кормления обязательно")
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @NotNull(message = "Тип кормления обязателен")
    private FeedingType type;

    @Min(value = 0, message = "Объём не может быть отрицательным")
    private Integer volumeMl;

    private String notes;
}
