package dev.emad.security.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

/**
 * @author EmadHanif
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper;

  public JwtAuthenticationEntryPoint(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public void commence(
      @NonNull HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException exception)
      throws IOException {

    response.setContentType("application/json");
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

    Map<String, ? extends Serializable> errorMessage =
        Map.of("message", exception.getMessage(), "timestamp", LocalDateTime.now());

    try (OutputStream out = response.getOutputStream()) {
      objectMapper.writeValue(out, errorMessage);
    }
  }
}
