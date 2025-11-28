package ai.parent.parentaibackend.ai.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Запрос к AI-чату.
 */
@Data
public class AiChatRequest {

    /**
     * Сообщение пользователя (вопрос).
     */
    @NotBlank(message = "Сообщение не может быть пустым")
    private String message;

    /**
     * Необязательный id ребёнка, к которому относится вопрос.
     * Если передан, сервис попробует найти ребёнка именно у текущего пользователя.
     */
    private Long babyId;
}
