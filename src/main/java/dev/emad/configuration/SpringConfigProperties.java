package dev.emad.configuration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.Set;

/**
 * @author EmadHanif
 */
@Validated
@Component
@Getter
@Setter
@ConfigurationProperties("config")
public class SpringConfigProperties {

  private final Security security = new Security();
  private final DatabaseConfig databaseConfig = new DatabaseConfig();
  private final ContextPath contextPath = new ContextPath();

  @Getter
  @Setter
  public static class DatabaseConfig {
    @NotBlank private String host;
    @NotBlank private String username;
    @NotBlank private String password;
  }

  @Getter
  @Setter
  public static class Security {
    @NotNull private Set<String> redirectUris;
    @NotBlank private String tokenEndpoint;
    @NotBlank private String authorizationEndpoint;
    @NotBlank private String issuer;
    @NotBlank private Set<String> cors;
  }

  @Getter
  @Setter
  public static class ContextPath {
    @NotBlank private String clientPath;
  }
}
