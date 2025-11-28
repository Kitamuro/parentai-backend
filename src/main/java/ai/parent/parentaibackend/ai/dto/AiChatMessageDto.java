package ai.parent.parentaibackend.ai.dto;

import ai.parent.parentaibackend.ai.AiMessageRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiChatMessageDto {

    private Long id;
    private AiMessageRole role;
    private String content;
    private LocalDateTime createdAt;
}
