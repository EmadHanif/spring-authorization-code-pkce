package dev.emad.configuration;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * @author EmadHanif
 */
@Validated
@Component
@Getter
@Setter
@ConfigurationProperties("jwt.keystore")
public class JwtKeyStoreProperties {
  @NotBlank private String jksLocation;
  @NotBlank private String password;
  @NotBlank private String keyAlias;
}
