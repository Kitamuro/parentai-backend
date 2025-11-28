package ai.parent.parentaibackend.ai;

import ai.parent.parentaibackend.baby.Baby;
import ai.parent.parentaibackend.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class AiChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Владелец диалога.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Опционально — ребёнок, к которому относится сообщение.
     */
    @ManyToOne
    @JoinColumn(name = "baby_id")
    private Baby baby;

    /**
     * Роль: USER или ASSISTANT.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AiMessageRole role;

    /**
     * Текст сообщения.
     */
    @Column(nullable = false, length = 4000)
    private String content;

    /**
     * Время создания.
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;
}
