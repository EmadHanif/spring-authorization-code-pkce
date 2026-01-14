package dev.emad.repositories;

import dev.emad.entities.User;
import java.util.Optional;
import java.util.UUID;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author EmadHanif
 */
public interface UserRepository extends JpaRepository<User, UUID> {

  @Query(
      """
        SELECT u FROM User u
        JOIN FETCH u.userRoleSet ur
        JOIN FETCH ur.role r
        WHERE u.email = LOWER(:email)
      """)
  Optional<User> findByEmail(@NonNull String email);
}
