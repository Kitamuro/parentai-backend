package ai.parent.parentaibackend.baby;

import ai.parent.parentaibackend.baby.dto.BabyResponse;

public class BabyMapper {

    public static BabyResponse toResponse(Baby baby) {
        if (baby == null) return null;
        return new BabyResponse(
                baby.getId(),
                baby.getName(),
                baby.getDateOfBirth(),
                baby.getGender(),
                baby.getNotes()
        );
    }
}
