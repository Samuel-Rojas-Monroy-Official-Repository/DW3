package jalau.cis.userapi;

import jalau.cis.userapi.dto.CreateUserRequest;
import jalau.cis.userapi.dto.UserResponse;
import jalau.cis.userapi.model.User;
import jalau.cis.userapi.repository.UserRepository;
import jalau.cis.userapi.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getUsers_shouldReturnPageOfUsers() {
        User user = new User("uuid-1", "Juan", "jcarlos", "hash");
        when(userRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(user)));

        var result = userService.getUsers(null, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals("Juan", result.getContent().get(0).getName());
    }

    @Test
    void getUserById_shouldReturnUser_whenExists() {
        User user = new User("uuid-1", "Juan", "jcarlos", "hash");
        when(userRepository.findById("uuid-1")).thenReturn(Optional.of(user));

        UserResponse response = userService.getUserById("uuid-1");

        assertEquals("Juan", response.getName());
        assertEquals("jcarlos", response.getLogin());
    }

    @Test
    void getUserById_shouldThrow_whenNotExists() {
        when(userRepository.findById("no-existe")).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> userService.getUserById("no-existe"));
    }

    @Test
    void createUser_shouldThrow_whenLoginExists() {
        when(userRepository.existsByLogin("jcarlos")).thenReturn(true);
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Juan");
        request.setLogin("jcarlos");
        request.setPassword("123456");

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(request));
    }

    @Test
    void createUser_happyPath() {
        when(userRepository.existsByLogin("newuser")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("hashed");
        when(userRepository.save(any(User.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        CreateUserRequest request = new CreateUserRequest();
        request.setName("Nuevo");
        request.setLogin("newuser");
        request.setPassword("123456");

        UserResponse response = userService.createUser(request);

        assertEquals("Nuevo", response.getName());
        assertEquals("newuser", response.getLogin());
    }
}
