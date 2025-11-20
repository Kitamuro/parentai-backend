package ai.parent.parentaibackend.tracking.feeding;

import ai.parent.parentaibackend.baby.Baby;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "feeding_events")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedingEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // какой ребёнок
    @ManyToOne(optional = false)
    @JoinColumn(name = "baby_id")
    private Baby baby;

    // время начала кормления
    @Column(nullable = false)
    private LocalDateTime startTime;

    // время окончания (может быть null, если фиксируем только момент)
    private LocalDateTime endTime;

    // тип кормления
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeedingType type;

    // объём в мл (для смеси/прикорма), можно null
    private Integer volumeMl;

    // комментарий
    private String notes;
}