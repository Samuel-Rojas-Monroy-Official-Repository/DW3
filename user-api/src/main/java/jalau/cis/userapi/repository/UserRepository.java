package jalau.cis.userapi.repository;

import jalau.cis.userapi.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByLogin(String login);
    boolean existsByLogin(String login);
    Page<User> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
