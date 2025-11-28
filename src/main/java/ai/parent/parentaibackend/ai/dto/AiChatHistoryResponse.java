package ai.parent.parentaibackend.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * История сообщений для чата.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiChatHistoryResponse {

    /**
     * Идентификатор ребёнка (может быть null, если история общая).
     */
    private Long babyId;

    /**
     * Список сообщений (обычно в порядке от новых к старым или наоборот).
     */
    private List<AiChatMessageDto> messages;
}
