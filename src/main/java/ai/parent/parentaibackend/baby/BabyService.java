package ai.parent.parentaibackend.baby;

import ai.parent.parentaibackend.baby.dto.CreateBabyRequest;
import ai.parent.parentaibackend.user.CurrentUserService;
import ai.parent.parentaibackend.user.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BabyService {

    private final BabyRepository babyRepository;
    private final CurrentUserService currentUserService;


    public Baby createBaby(CreateBabyRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Baby name is required");
        }

        User currentUser = currentUserService.getCurrentUserOrThrow();

        Baby baby = new Baby();
        baby.setName(request.getName());
        baby.setDateOfBirth(request.getDateOfBirth());
        baby.setGender(request.getGender());
        baby.setNotes(request.getNotes());
        baby.setUser(currentUser);

        return babyRepository.save(baby);
    }


    public List<Baby> getAllBabies() {
        User currentUser = currentUserService.getCurrentUserOrThrow();
        return babyRepository.findByUser(currentUser);
    }

    public Optional<Baby> getBabyById(Long id) {
        User currentUser = currentUserService.getCurrentUserOrThrow();
        return babyRepository.findByIdAndUser(id, currentUser);
    }
}