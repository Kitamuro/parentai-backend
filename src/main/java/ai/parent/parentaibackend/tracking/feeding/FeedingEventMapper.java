package ai.parent.parentaibackend.tracking.feeding;

import ai.parent.parentaibackend.tracking.feeding.dto.FeedingEventResponse;

public class FeedingEventMapper {

    public static FeedingEventResponse toResponse(FeedingEvent event) {
        if (event == null) return null;

        return new FeedingEventResponse(
                event.getId(),
                event.getBaby() != null ? event.getBaby().getId() : null,
                event.getStartTime(),
                event.getEndTime(),
                event.getType(),
                event.getVolumeMl(),
                event.getNotes()
        );
    }
}
