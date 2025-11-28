package ai.parent.parentaibackend.status;

import ai.parent.parentaibackend.baby.Baby;
import ai.parent.parentaibackend.baby.BabyRepository;
import ai.parent.parentaibackend.baby.dto.BabySummaryDto;
import ai.parent.parentaibackend.common.exception.ResourceNotFoundException;
import ai.parent.parentaibackend.user.CurrentUserService;
import ai.parent.parentaibackend.user.User;
import ai.parent.parentaibackend.user.dto.UserSummaryDto;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/me")
@AllArgsConstructor
public class MeController {

    private final CurrentUserService currentUserService;
    private final BabyRepository babyRepository;
    private final TodayStatusService todayStatusService;

    /**
     * Главный эндпоинт для мобильного приложения.
     *
     * GET /api/me/dashboard?date=2025-11-27&babyId=1
     *
     * Если date не передана – берём сегодня.
     * Если babyId не передан – берём первого ребёнка пользователя (если есть).
     */
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,
            @RequestParam(required = false)
            Long babyId
    ) {
        try {
            User currentUser = currentUserService.getCurrentUserOrThrow();
            LocalDate targetDate = (date != null) ? date : LocalDate.now();

            // Все дети текущего пользователя
            List<Baby> babies = babyRepository.findByUser(currentUser);

            List<BabySummaryDto> babyDtos = babies.stream()
                    .map(b -> new BabySummaryDto(
                            b.getId(),
                            b.getName(),
                            b.getDateOfBirth(),
                            b.getGender(),
                            b.getNotes()
                    ))
                    .collect(Collectors.toList());

            // Выбранный ребёнок (из параметра или первый)
            Long selectedBabyId = babyId;
            if (selectedBabyId == null && !babies.isEmpty()) {
                selectedBabyId = babies.get(0).getId();
            }

            // финальная копия для использования в lambda
            final Long finalBabyId = selectedBabyId;

            TodayStatusResponse todayStatus = null;

            if (finalBabyId != null) {

                boolean ownsBaby = babies.stream()
                        .anyMatch(b -> b.getId().equals(finalBabyId));

                if (!ownsBaby) {
                    throw new ResourceNotFoundException(
                            "Ребёнок с id=" + finalBabyId + " не найден у текущего пользователя"
                    );
                }

                todayStatus = todayStatusService.getTodayStatus(finalBabyId, targetDate);
            }

            UserSummaryDto userDto = new UserSummaryDto(
                    currentUser.getId(),
                    currentUser.getEmail(),
                    currentUser.getFullName()
            );

            MeDashboardResponse response = new MeDashboardResponse(
                    userDto,
                    babyDtos,
                    selectedBabyId, // можно вернуть исходный
                    targetDate,
                    todayStatus
            );

            return ResponseEntity.ok(response);

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


}
