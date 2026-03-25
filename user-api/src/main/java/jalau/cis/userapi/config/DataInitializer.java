package jalau.cis.userapi.config;

import jalau.cis.userapi.model.User;
import jalau.cis.userapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        Optional<User> existing = userRepository.findByLogin("admin");
        if (existing.isPresent()) {
            User user = existing.get();
            user.setPassword(passwordEncoder.encode("admin123"));
            userRepository.save(user);
            System.out.println(">>> Usuario admin ya existe");
            return;
        }
        User admin = new User(
            UUID.randomUUID().toString(),
            "Administrador",
            "admin",
            passwordEncoder.encode("admin123")
        );
        userRepository.save(admin);
        System.out.println(">>> Usuario admin creado correctamente");
    }
}
