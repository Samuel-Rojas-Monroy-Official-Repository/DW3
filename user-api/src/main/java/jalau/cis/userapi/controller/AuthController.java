package jalau.cis.userapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jalau.cis.userapi.dto.LoginRequest;
import jalau.cis.userapi.dto.LoginResponse;
import jalau.cis.userapi.repository.UserRepository;
import jalau.cis.userapi.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Autenticacion y obtencion de JWT")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Operation(summary = "Iniciar sesion", description = "Retorna un JWT valido para usar en endpoints protegidos")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        var user = userRepository.findByLogin(request.getLogin())
                .orElse(null);

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("Login o password incorrecto");
        }

        String token = jwtUtil.generateToken(user.getLogin());
        return ResponseEntity.ok(new LoginResponse(token, 86400000));
    }
}
