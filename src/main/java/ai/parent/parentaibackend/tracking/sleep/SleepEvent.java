package ai.parent.parentaibackend.tracking.sleep;

import ai.parent.parentaibackend.baby.Baby;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "sleep_events")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SleepEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "baby_id")
    private Baby baby;

    // время засыпания
    @Column(nullable = false)
    private LocalDateTime startTime;

    // время пробуждения
    private LocalDateTime endTime;

    // тип сна: DAY / NIGHT
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SleepType type;

    // заметки
    private String notes;
}
