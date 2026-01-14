package dev.emad.controllers;

import io.reactivex.rxjava3.core.Single;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author EmadHanif
 */
@RestController
@RequestMapping("/api/v1/example")
public class ExampleController {

  @GetMapping("/m1")
  public Map<String, Object> m1() {
    return Map.of("message", "This method is executed without any authentication.");
  }

  @GetMapping("/m2")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  public Map<String, Object> m2() {
    return Map.of("message", "You have the ROLE_ADMIN access.");
  }

  @GetMapping("/m3")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  public Single<Map<String, Object>> m3() {
    return Single.just(Map.of("message", "Everything is working fine..."));
  }
}
