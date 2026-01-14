package dev.emad.repositories;

import dev.emad.entities.Role;
import java.util.Optional;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author EmadHanif
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

  @Query(
      """
          SELECT r FROM Role r
          WHERE r.name =:name
          """)
  Optional<Role> findByName(@NonNull String name);
}
