package dev.emad.entities.relationship;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.emad.entities.Role;
import dev.emad.entities.User;
import dev.emad.entities.relationship.key.UserRoleKey;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author EmadHanif
 */
@Getter
@Setter
@NoArgsConstructor
@Entity(name = "UserRole")
@Table(name = "user_roles")
public class UserRole implements Serializable {

  @Serial private static final long serialVersionUID = -7722434072546680548L;

  @EmbeddedId private UserRoleKey id;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("userId")
  @JoinColumn(name = "user_fk")
  @NotNull(message = "User cannot be null.")
  @JsonIgnore // Added to avoid infinite recursion
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("roleId")
  @JoinColumn(name = "role_fk")
  @NotNull(message = "Role cannot be null.")
  private Role role;

  public UserRole(User user, Role role) {
    this.id = new UserRoleKey(user.getId(), role.getId());
    this.user = user;
    this.role = role;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    UserRole userRole = (UserRole) o;
    return Objects.equals(id, userRole.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
