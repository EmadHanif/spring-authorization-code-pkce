package dev.emad.security.config;

import dev.emad.configuration.SpringConfigProperties;

import java.util.ArrayList;
import java.util.Arrays;

import dev.emad.security.filter.JwtAuthenticationEntryPoint;
import dev.emad.security.filter.JwtPreprocessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @author EmadHanif
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class ResourceServerConfig {

  private final JwtPreprocessor jwtPreprocessor;
  private final SpringConfigProperties springConfigProperties;

  public ResourceServerConfig(
      JwtPreprocessor jwtPreprocessor, SpringConfigProperties springConfigProperties) {
    this.jwtPreprocessor = jwtPreprocessor;
    this.springConfigProperties = springConfigProperties;
  }

  @Bean
  @Order(2)
  public SecurityFilterChain asLoginFilterChain(HttpSecurity http) throws Exception {
    return http.securityMatcher("/login")
        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
        .formLogin(form -> form.loginPage("/login").permitAll()) // (imp) necessary
        .sessionManagement(
            sessionConfigurer ->
                sessionConfigurer.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
        .csrf(AbstractHttpConfigurer::disable) // disabling csrf,
        .build();
  }

  @Bean
  @Order(3)
  public SecurityFilterChain asResourceFilterChain(
      HttpSecurity http,
      JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
      JwtAuthenticationConverter jwtAuthenticationConverter,
      CorsConfigurationSource corsConfiguration) {
    return http.authorizeHttpRequests(
            auth ->
                auth.requestMatchers(HttpMethod.GET, "/api/v1/example/m1")
                    .permitAll()
                    .requestMatchers("/api/v1/**")
                    .hasAuthority("SCOPE_user")
                    .anyRequest()
                    .authenticated())
        .oauth2ResourceServer(
            oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)))
        .sessionManagement(
            sessionConfigurer ->
                sessionConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .csrf(AbstractHttpConfigurer::disable) // disabling csrf,
        .cors(cors -> cors.configurationSource(corsConfiguration))
        .addFilterBefore(jwtPreprocessor, UsernamePasswordAuthenticationFilter.class)
        .securityContext(configurer -> configurer.requireExplicitSave(false))
        .exceptionHandling(
            exceptionHandling ->
                exceptionHandling.authenticationEntryPoint(jwtAuthenticationEntryPoint))
        .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
        .build();
  }

  @Bean
  public CorsConfigurationSource corsConfiguration() {

    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(
        new ArrayList<>(springConfigProperties.getSecurity().getCors()));
    configuration.setAllowCredentials(true);
    configuration.setAllowedMethods(
        Arrays.asList("GET", "POST", "OPTIONS", "PUT", "PATCH", "DELETE", "HEAD"));
    configuration.setAllowedHeaders(
        Arrays.asList(
            "Origin",
            "Content-Type",
            "Accept",
            "Access-Control-Allow-Headers",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers",
            "X-Requested-With",
            "Authorization"));
    configuration.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    return source;
  }

  @Bean
  public CorsFilter corsFilter(CorsConfigurationSource corsConfiguration) {
    return new CorsFilter(corsConfiguration);
  }
}
