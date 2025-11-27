package ai.parent.parentaibackend.baby;

import ai.parent.parentaibackend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BabyRepository extends JpaRepository<Baby, Long> {
    Optional<Baby> findByIdAndUser(Long babyId, User currentUser);

    List<Baby> findByUser(User user);
}
