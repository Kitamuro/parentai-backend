package ai.parent.parentaibackend.baby;

import ai.parent.parentaibackend.baby.dto.CreateBabyRequest;
import ai.parent.parentaibackend.baby.dto.UpdateBabyRequest;
import ai.parent.parentaibackend.common.exception.ResourceNotFoundException;
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

    public Baby updateBaby(Long id, UpdateBabyRequest request) {
        User currentUser = currentUserService.getCurrentUserOrThrow();

        Baby baby = babyRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ребёнок с id=" + id + " не найден у текущего пользователя"
                ));

        if (request.getName() != null && !request.getName().isBlank()) {
            baby.setName(request.getName());
        }
        if (request.getDateOfBirth() != null) {
            baby.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getGender() != null) {
            baby.setGender(request.getGender());
        }
        if (request.getNotes() != null) {
            baby.setNotes(request.getNotes());
        }

        return babyRepository.save(baby);
    }

    public void deleteBaby(Long id) {
        User currentUser = currentUserService.getCurrentUserOrThrow();

        Baby baby = babyRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ребёнок с id=" + id + " не найден у текущего пользователя"
                ));

        babyRepository.delete(baby);
    }
}