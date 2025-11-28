package ai.parent.parentaibackend.ai;

import ai.parent.parentaibackend.ai.dto.AiChatHistoryResponse;
import ai.parent.parentaibackend.ai.dto.AiChatMessageDto;
import ai.parent.parentaibackend.ai.dto.AiChatRequest;
import ai.parent.parentaibackend.ai.dto.AiChatResponse;
import ai.parent.parentaibackend.baby.Baby;
import ai.parent.parentaibackend.baby.BabyRepository;
import ai.parent.parentaibackend.common.exception.ResourceNotFoundException;
import ai.parent.parentaibackend.user.CurrentUserService;
import ai.parent.parentaibackend.user.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AiChatService {

    private final CurrentUserService currentUserService;
    private final BabyRepository babyRepository;
    private final AiChatMessageRepository aiChatMessageRepository;

    public AiChatResponse chat(AiChatRequest request) {
        User currentUser = currentUserService.getCurrentUserOrThrow();

        Baby baby = null;
        String babyAgeDescription = null;

        if (request.getBabyId() != null) {
            baby = babyRepository.findByIdAndUser(request.getBabyId(), currentUser)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Ребёнок с id=" + request.getBabyId() + " не найден у текущего пользователя"
                    ));

            babyAgeDescription = buildAgeDescription(baby.getDateOfBirth());
        }

        // 1. Сохраняем пользовательское сообщение
        AiChatMessage userMessage = new AiChatMessage();
        userMessage.setUser(currentUser);
        userMessage.setBaby(baby);
        userMessage.setRole(AiMessageRole.USER);
        userMessage.setContent(request.getMessage());
        userMessage.setCreatedAt(LocalDateTime.now());
        aiChatMessageRepository.save(userMessage);

        // 2. Генерируем заглушку-ответ
        String reply = buildStubReply(currentUser, baby, babyAgeDescription, request.getMessage());

        // 3. Сохраняем ответ ассистента
        AiChatMessage assistantMessage = new AiChatMessage();
        assistantMessage.setUser(currentUser);
        assistantMessage.setBaby(baby);
        assistantMessage.setRole(AiMessageRole.ASSISTANT);
        assistantMessage.setContent(reply);
        assistantMessage.setCreatedAt(LocalDateTime.now());
        aiChatMessageRepository.save(assistantMessage);

        // 4. Собираем DTO-ответ
        AiChatResponse response = new AiChatResponse();
        response.setReply(reply);
        response.setBabyId(baby != null ? baby.getId() : null);
        response.setBabyName(baby != null ? baby.getName() : null);
        response.setBabyAge(babyAgeDescription);

        return response;
    }

    /**
     * Генерация "заглушки" ответа.
     * Здесь в будущем можно будет заменить логику на реальный вызов LLM.
     */
    private String buildStubReply(User user, Baby baby, String babyAgeDescription, String message) {
        StringBuilder sb = new StringBuilder();

        sb.append("Привет! Я AI-ассистент ParentAI. ");
        sb.append("Сейчас я работаю в режиме тестовой заглушки, ");
        sb.append("поэтому отвечаю общими рекомендациями, а не реальным ИИ.\n\n");

        sb.append("Вы спросили: \"").append(message).append("\".\n\n");

        if (baby != null) {
            sb.append("Я вижу, что вопрос касается ребёнка ")
                    .append(baby.getName() != null ? baby.getName() : " (имя не указано)");

            if (babyAgeDescription != null) {
                sb.append(", возраст: ").append(babyAgeDescription);
            }
            sb.append(".\n");
        }

        sb.append("Старайтесь опираться на режим сна и кормления, ");
        sb.append("наблюдать за сигналами усталости (протирает глаза, зевает, становится более капризным). ");
        sb.append("Не забывайте, что мои ответы не являются медицинской рекомендацией — ");
        sb.append("при любых сомнениях лучше обсудить ситуацию с педиатром.\n\n");

        sb.append("В следующих версиях я смогу анализировать реальные треки сна, кормлений и памперсов и давать более точные подсказки именно под вашего ребёнка.");

        return sb.toString();
    }

    /**
     * Строим человекочитаемое описание возраста ("9 месяцев", "1 год 2 месяца").
     */
    private String buildAgeDescription(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            return null;
        }
        LocalDate now = LocalDate.now();
        if (dateOfBirth.isAfter(now)) {
            return null;
        }
        Period p = Period.between(dateOfBirth, now);

        int years = p.getYears();
        int months = p.getMonths();

        if (years == 0 && months == 0) {
            return "меньше месяца";
        }

        StringBuilder sb = new StringBuilder();
        if (years > 0) {
            sb.append(years).append(" ").append(yearWord(years));
        }
        if (months > 0) {
            if (!sb.isEmpty()) {
                sb.append(" ");
            }
            sb.append(months).append(" ").append(monthWord(months));
        }
        return sb.toString();
    }

    private String yearWord(int years) {
        int last = years % 10;
        int last2 = years % 100;
        if (last == 1 && last2 != 11) return "год";
        if (last >= 2 && last <= 4 && !(last2 >= 12 && last2 <= 14)) return "года";
        return "лет";
    }

    private String monthWord(int months) {
        int last = months % 10;
        int last2 = months % 100;
        if (last == 1 && last2 != 11) return "месяц";
        if (last >= 2 && last <= 4 && !(last2 >= 12 && last2 <= 14)) return "месяца";
        return "месяцев";
    }

    public AiChatHistoryResponse getHistory(Long babyId) {
        User currentUser = currentUserService.getCurrentUserOrThrow();

        List<AiChatMessage> messages;

        if (babyId == null) {
            // Общая история пользователя
            messages = aiChatMessageRepository.findByUserOrderByCreatedAtDesc(currentUser);
        } else {
            Baby baby = babyRepository.findByIdAndUser(babyId, currentUser)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Ребёнок с id=" + babyId + " не найден у текущего пользователя"
                    ));

            messages = aiChatMessageRepository.findByUserAndBabyOrderByCreatedAtDesc(currentUser, baby);
        }

        List<AiChatMessageDto> dtos = messages.stream()
                .map(m -> new AiChatMessageDto(
                        m.getId(),
                        m.getRole(),
                        m.getContent(),
                        m.getCreatedAt()
                ))
                .toList();

        return new AiChatHistoryResponse(babyId, dtos);
    }
}
