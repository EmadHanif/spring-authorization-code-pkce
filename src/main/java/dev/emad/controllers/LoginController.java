package dev.emad.controllers;

import dev.emad.configuration.SpringConfigProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author EmadHanif
 */
@Controller
public class LoginController {

  private final SpringConfigProperties springConfigProperties;

  public LoginController(SpringConfigProperties springConfigProperties) {
    this.springConfigProperties = springConfigProperties;
  }

  @GetMapping("/login")
  public String login(HttpServletRequest request) {
    // Check if OAuth2 Authorization Request exists in session
    HttpSession session = request.getSession(false);
    if (session != null) {
      Object authRequest = session.getAttribute("SPRING_SECURITY_SAVED_REQUEST");
      System.out.println(authRequest);
      if (authRequest != null) {
        // User came from /oauth2/authorize ==> show login page
        return "login";
      }
    }

    // Default fallback
    return String.format(
        "redirect:%s/login", this.springConfigProperties.getContextPath().getClientPath());
  }
}
