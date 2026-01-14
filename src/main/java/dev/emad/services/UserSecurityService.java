package dev.emad.services;

import dev.emad.entities.User;
import dev.emad.repositories.UserRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author EmadHanif
 */
@Service
public class UserSecurityService implements UserDetailsService {

  private final UserRepository userRepository;

  public UserSecurityService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  @NonNull
  public User loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
    return this.userRepository
        .findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
  }
}
