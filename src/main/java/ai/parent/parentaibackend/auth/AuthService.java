package ai.parent.parentaibackend.auth;

import ai.parent.parentaibackend.auth.dto.LoginRequest;
import ai.parent.parentaibackend.auth.dto.RegisterRequest;
import ai.parent.parentaibackend.user.CustomUserDetails;
import ai.parent.parentaibackend.user.User;
import ai.parent.parentaibackend.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already registered");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());

        String hash = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());
        user.setPasswordHash(hash);

        return userRepository.save(user);
    }

    public CustomUserDetails authenticate(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return new CustomUserDetails(user);
    }
}