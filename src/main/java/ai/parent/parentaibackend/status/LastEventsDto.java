package ai.parent.parentaibackend.status;

import ai.parent.parentaibackend.tracking.diaper.DiaperEntry;
import ai.parent.parentaibackend.tracking.feeding.FeedingEvent;
import ai.parent.parentaibackend.tracking.sleep.SleepEvent;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LastEventsDto {
    private SleepEvent lastSleep;
    private FeedingEvent lastFeeding;
    private DiaperEntry lastDiaper;
}
