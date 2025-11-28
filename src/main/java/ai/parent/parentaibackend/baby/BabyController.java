package ai.parent.parentaibackend.baby;


import ai.parent.parentaibackend.baby.dto.BabyResponse;
import ai.parent.parentaibackend.baby.dto.CreateBabyRequest;
import ai.parent.parentaibackend.baby.dto.UpdateBabyRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/babies")
@AllArgsConstructor
public class BabyController {
    private final BabyService babyService;

    @PostMapping
    public ResponseEntity<BabyResponse> createBaby(@Valid @RequestBody CreateBabyRequest request) {
        Baby saved = babyService.createBaby(request);
        return ResponseEntity.ok(BabyMapper.toResponse(saved));
    }

    @GetMapping
    public List<BabyResponse> getAllBabies() {
        return babyService.getAllBabies()
                .stream()
                .map(BabyMapper::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BabyResponse> getBaby(@PathVariable Long id) {
        return babyService.getBabyById(id)
                .map(b -> ResponseEntity.ok(BabyMapper.toResponse(b)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BabyResponse> updateBaby(
            @PathVariable Long id,
            @RequestBody UpdateBabyRequest request
    ) {
        Baby updated = babyService.updateBaby(id, request);
        return ResponseEntity.ok(BabyMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBaby(@PathVariable Long id) {
        babyService.deleteBaby(id);
        return ResponseEntity.noContent().build();
    }
}
