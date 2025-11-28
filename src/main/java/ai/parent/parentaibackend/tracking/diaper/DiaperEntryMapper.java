package ai.parent.parentaibackend.tracking.diaper;

import ai.parent.parentaibackend.tracking.diaper.dto.DiaperEntryResponse;

public class DiaperEntryMapper {

    public static DiaperEntryResponse toResponse(DiaperEntry entry) {
        if (entry == null) return null;

        return new DiaperEntryResponse(
                entry.getId(),
                entry.getBaby() != null ? entry.getBaby().getId() : null,
                entry.getTime(),
                entry.getType(),
                entry.getNotes()
        );
    }
}
