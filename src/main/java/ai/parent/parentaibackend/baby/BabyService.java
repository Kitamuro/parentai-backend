package ai.parent.parentaibackend.baby;

import ai.parent.parentaibackend.baby.dto.CreateBabyRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BabyService {

    private final BabyRepository babyRepository;

    public Baby createBaby(CreateBabyRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Baby name is required");
        }

        Baby baby = new Baby();
        baby.setName(request.getName());
        baby.setDateOfBirth(request.getDateOfBirth());
        baby.setGender(request.getGender());
        baby.setNotes(request.getNotes());

        return babyRepository.save(baby);
    }

    public List<Baby> getAllBabies() {
        return babyRepository.findAll();
    }

    public Optional<Baby> getBabyById(Long id) {
        return babyRepository.findById(id);
    }
}