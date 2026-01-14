package dev.emad.entities.relationship.key;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author EmadHanif
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class UserRoleKey implements Serializable {

  @Serial private static final long serialVersionUID = -4152746239696582498L;

  // Use Snowflake-like implementation
  @Column(name = "user_fk", nullable = false)
  private Long userId;

  @Column(name = "role_fk", nullable = false)
  private Long roleId;

  public UserRoleKey(Long userId, Long roleId) {
    this.userId = userId;
    this.roleId = roleId;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    UserRoleKey that = (UserRoleKey) o;
    return Objects.equals(userId, that.userId) && Objects.equals(roleId, that.roleId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, roleId);
  }
}
