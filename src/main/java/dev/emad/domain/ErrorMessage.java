package dev.emad.domain;

import java.time.LocalDateTime;
import lombok.Getter;

/**
 * @author EmadHanif
 */
// Good Practice - Create a centralized exception handler & utilize this class
@Getter
public class ErrorMessage {
  public final String message;
  private final LocalDateTime timestamp;

  public ErrorMessage(String message, LocalDateTime timestamp) {
    this.message = message;
    this.timestamp = timestamp;
  }
}
