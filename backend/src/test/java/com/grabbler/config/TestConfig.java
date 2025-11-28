package com.grabbler.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@TestConfiguration
public class TestConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  // Optional: If you want to disable security completely for some tests
  // Uncomment this if tests still fail
  /*
   * @Bean
   *
   * @Primary
   * public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws
   * Exception {
   * http
   * .csrf(csrf -> csrf.disable())
   * .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
   * return http.build();
   * }
   */
}
