package ai.parent.parentaibackend.user;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Получает текущего пользователя из JWT.
     * Используется в сервисах, чтобы проверять владельца данных.
     */
    public User getCurrentUserOrThrow() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails principal)) {
            throw new IllegalStateException("Пользователь не авторизован");
        }

        Long userId = principal.getId();

        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalStateException("Пользователь не найден в базе: id=" + userId));
    }
}
