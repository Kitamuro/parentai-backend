package ai.parent.parentaibackend.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Ответ AI-чата.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiChatResponse {

    /**
     * Сгенерированный ответ ассистента.
     */
    private String reply;

    /**
     * Использованный id ребёнка (если был указан и найден).
     */
    private Long babyId;

    /**
     * Имя ребёнка (если найден).
     */
    private String babyName;

    /**
     * Текстовое описание возраста (например, "9 месяцев", "2 года 3 месяца").
     */
    private String babyAge;
}
