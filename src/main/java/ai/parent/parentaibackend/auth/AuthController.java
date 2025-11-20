package ai.parent.parentaibackend.auth;

import ai.parent.parentaibackend.auth.dto.AuthResponse;
import ai.parent.parentaibackend.auth.dto.LoginRequest;
import ai.parent.parentaibackend.auth.dto.RegisterRequest;
import ai.parent.parentaibackend.user.CustomUserDetails;
import ai.parent.parentaibackend.user.User;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            User saved = authService.register(request);
            return ResponseEntity.ok("OK");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            CustomUserDetails userDetails = authService.authenticate(request);
            String token = jwtService.generateToken(userDetails);
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
