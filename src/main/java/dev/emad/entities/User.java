package dev.emad.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.emad.entities.relationship.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

/**
 * @author EmadHanif
 */
@Table(name = "users")
@Entity(name = "User")
@NoArgsConstructor
public class User implements UserDetails, Serializable {

  @Serial private static final long serialVersionUID = -4371035839291790831L;

  // Use snowflake-like implementation
  @Getter
  @Setter
  @Id
  @Column(updatable = false)
  private Long id;

  @Getter
  @Column(nullable = false, length = 50)
  @NotBlank(message = "Full name is required")
  @Length(min = 3, max = 20, message = "Full name min. length is 3 and max length is 20.")
  private String fullName;

  @Setter
  @Column(nullable = false)
  private String password;

  @Getter
  @Column(nullable = false, unique = true)
  @NotBlank(message = "Email is required.")
  @Email(message = "Email appears to be invalid")
  private String email;

  // User Role
  @Getter
  @OneToMany(
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      mappedBy = "user")
  @JsonIgnore // Added to avoid infinite recursion
  private Set<UserRole> userRoleSet = new HashSet<>();

  @Transient private Collection<? extends GrantedAuthority> authorities;

  public void setFullName(String fullName) {
    this.fullName = StringUtils.capitalize(fullName).strip();
  }

  public void setEmail(String email) {
    this.email = email.toLowerCase().strip();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    Set<GrantedAuthority> authorities = new HashSet<>();
    userRoleSet.forEach(
        user ->
            authorities.add(new SimpleGrantedAuthority(user.getRole().getName().toUpperCase())));
    return authorities;
  }

  public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
    this.authorities = authorities;
  }

  @Override
  public @Nullable String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  // Must be added to database fields (a good prc. is to use one-to-one table)
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    User user = (User) o;
    return Objects.equals(id, user.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

  // Convenience Methods
  public void addUserRole(Role role) {
    if (Objects.isNull(role)) throw new RuntimeException("Role is null.");
    UserRole userRole = new UserRole(this, role);
    userRoleSet.add(userRole);
  }
}
