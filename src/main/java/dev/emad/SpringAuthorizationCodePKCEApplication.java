package dev.emad;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;
import java.util.TimeZone;

/**
 * @author EmadHanif
 */
@SpringBootApplication
@Slf4j
public class SpringAuthorizationCodePKCEApplication {

  public static void main(String[] args) {
    SpringApplication.run(SpringAuthorizationCodePKCEApplication.class, args);
  }

  @PostConstruct
  public void init() {
    // Set default timezone to UTC
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    log.info("Spring Boot application running in UTC timezone: {}", LocalDateTime.now());
  }
}
