package ai.parent.parentaibackend.ai;

import ai.parent.parentaibackend.baby.Baby;
import ai.parent.parentaibackend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AiChatMessageRepository extends JpaRepository<AiChatMessage, Long> {

    // Вся история пользователя, свежие сверху
    List<AiChatMessage> findByUserOrderByCreatedAtDesc(User user);

    // История по конкретному ребёнку
    List<AiChatMessage> findByUserAndBabyOrderByCreatedAtDesc(User user, Baby baby);
}
