package ai.parent.parentaibackend.tracking.diaper;

import ai.parent.parentaibackend.baby.Baby;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "diaper_entries")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiaperEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "baby_id")
    private Baby baby;

    @Column(nullable = false)
    private LocalDateTime time;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiaperType type;

    private String notes;
}