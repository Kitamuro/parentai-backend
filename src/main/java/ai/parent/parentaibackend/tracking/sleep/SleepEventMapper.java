package ai.parent.parentaibackend.tracking.sleep;

import ai.parent.parentaibackend.tracking.sleep.dto.SleepEventResponse;

public class SleepEventMapper {

    public static SleepEventResponse toResponse(SleepEvent event) {
        if (event == null) return null;

        return new SleepEventResponse(
                event.getId(),
                event.getBaby() != null ? event.getBaby().getId() : null,
                event.getStartTime(),
                event.getEndTime(),
                event.getType(),
                event.getNotes()
        );
    }
}
