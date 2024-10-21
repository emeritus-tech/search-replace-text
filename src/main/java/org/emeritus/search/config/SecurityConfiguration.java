package org.emeritus.search.config;

import org.emeritus.search.lti.common.Lti13Configurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * The Class SecurityConfiguration.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

  /**
   * Filter chain.
   *
   * @param httpSecurity the httpSecurity
   * @return the security filter chain
   * @throws Exception the exception
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(CsrfConfigurer::disable)
        .authorizeHttpRequests(httpRequests -> httpRequests.anyRequest().permitAll());
    Lti13Configurer lti13Configurer = new Lti13Configurer();
    http.apply(lti13Configurer);
    return http.build();
  }
}
