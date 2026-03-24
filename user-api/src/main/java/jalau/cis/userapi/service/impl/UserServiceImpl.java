package jalau.cis.userapi.service.impl;

import jalau.cis.userapi.dto.CreateUserRequest;
import jalau.cis.userapi.dto.UpdateUserRequest;
import jalau.cis.userapi.dto.UserResponse;
import jalau.cis.userapi.model.User;
import jalau.cis.userapi.repository.UserRepository;
import jalau.cis.userapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getLogin());
    }

    @Override
    public Page<UserResponse> getUsers(String name, Pageable pageable) {
        if (name != null && !name.isBlank()) {
            return userRepository.findByNameContainingIgnoreCase(name, pageable).map(this::toResponse);
        }
        return userRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario con id '" + id + "' no encontrado"));
        return toResponse(user);
    }

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByLogin(request.getLogin())) {
            throw new IllegalArgumentException("El login '" + request.getLogin() + "' ya esta registrado");
        }
        User user = new User(
                UUID.randomUUID().toString(),
                request.getName(),
                request.getLogin(),
                passwordEncoder.encode(request.getPassword())
        );
        return toResponse(userRepository.save(user));
    }

    @Override
    public UserResponse updateUser(String id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario con id '" + id + "' no encontrado"));
        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        return toResponse(userRepository.save(user));
    }

    @Override
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new NoSuchElementException("Usuario con id '" + id + "' no encontrado");
        }
        userRepository.deleteById(id);
    }
}
