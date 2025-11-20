package ai.parent.parentaibackend.baby;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "babies")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Baby {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private LocalDate dateOfBirth;

    private String gender;

    private String notes;
}
