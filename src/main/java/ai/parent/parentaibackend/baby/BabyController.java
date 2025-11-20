package ai.parent.parentaibackend.baby;

import ai.parent.parentaibackend.baby.dto.CreateBabyRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/babies")
@AllArgsConstructor
public class BabyController {
    private final BabyService babyService;

    @PostMapping
    public ResponseEntity<?> createBaby(@RequestBody CreateBabyRequest request) {
        try {
            Baby saved = babyService.createBaby(request);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public List<Baby> getAllBabies() {
        return babyService.getAllBabies();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Baby> getBaby(@PathVariable Long id) {
        return babyService.getBabyById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
