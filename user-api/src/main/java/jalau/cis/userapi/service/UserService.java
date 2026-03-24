package jalau.cis.userapi.service;

import jalau.cis.userapi.dto.CreateUserRequest;
import jalau.cis.userapi.dto.UpdateUserRequest;
import jalau.cis.userapi.dto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Page<UserResponse> getUsers(String name, Pageable pageable);
    UserResponse getUserById(String id);
    UserResponse createUser(CreateUserRequest request);
    UserResponse updateUser(String id, UpdateUserRequest request);
    void deleteUser(String id);
}
