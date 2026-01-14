package dev.emad.security.initializer;

import dev.emad.configuration.SpringConfigProperties;
import dev.emad.entities.Role;
import dev.emad.entities.User;
import dev.emad.repositories.RoleRepository;
import dev.emad.repositories.UserRepository;
import java.util.Objects;
import java.util.UUID;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

/**
 * @author EmadHanif
 */
@Configuration
public class OAuth2ClientInitializer {

  private final RegisteredClientRepository registeredClientRepository;
  private final RoleRepository roleRepository;
  private final UserRepository userRepository;
  private final SpringConfigProperties springConfigProperties;
  private final PasswordEncoder passwordEncoder;
  private final ClientSettings clientSettings;
  private final TokenSettings tokenSettings;

  public OAuth2ClientInitializer(
      RegisteredClientRepository registeredClientRepository,
      RoleRepository roleRepository,
      UserRepository userRepository,
      SpringConfigProperties springConfigProperties,
      PasswordEncoder passwordEncoder,
      ClientSettings clientSettings,
      TokenSettings tokenSettings) {
    this.registeredClientRepository = registeredClientRepository;
    this.roleRepository = roleRepository;
    this.userRepository = userRepository;
    this.springConfigProperties = springConfigProperties;
    this.passwordEncoder = passwordEncoder;
    this.clientSettings = clientSettings;
    this.tokenSettings = tokenSettings;
  }

  @Bean
  @EventListener(ApplicationReadyEvent.class)
  public void initializeClients() {
    RegisteredClient existingClient = this.registeredClientRepository.findByClientId("client");

    if (Objects.isNull(existingClient)) {
      RegisteredClient registeredClient =
          RegisteredClient.withId(UUID.randomUUID().toString())
              .clientId("client")
              .clientSecret(passwordEncoder.encode("secret"))
              .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
              .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
              .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
              .redirectUris(
                  uris -> uris.addAll(springConfigProperties.getSecurity().getRedirectUris()))
              .scope(OidcScopes.OPENID)
              .scope("user")
              .tokenSettings(tokenSettings)
              .clientSettings(clientSettings)
              .build();

      this.registeredClientRepository.save(registeredClient);
    }

    // For ROLE_ADMIN and creating demo user i.e., david_freed@gmail.com

    // Create ROLE_ADMIN if it doesn't exist
    Role role =
        this.roleRepository
            .findByName("ROLE_ADMIN")
            .orElseGet(
                () -> {
                  System.out.println("=== Initializing ROLE_ADMIN ===");
                  return this.roleRepository.save(new Role("ROLE_ADMIN"));
                });

    this.userRepository
        .findByEmail("david_freed@gmail.com")
        .orElseGet(
            () -> {
              System.out.println("=== Initializing User ===");
              User user = new User();
              user.setId(1L);
              user.setFullName("David Freed");
              user.setEmail("david_freed@gmail.com");
              user.setPassword(this.passwordEncoder.encode("adminadmin"));

              // Add User Role
              user.addUserRole(role);

              // Persist
              return this.userRepository.save(user);
            });
  }
}
