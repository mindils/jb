package ru.mindils.jb.core.config;

import lombok.RequiredArgsConstructor;
import org.springdoc.webmvc.core.service.RequestService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

  private final CustomAuthenticationFailureHandler authenticationFailureHandler;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http, RequestService requestBuilder)
      throws Exception {
    http.authorizeHttpRequests(request -> request
            .requestMatchers("/registration**", "/login**", "/css/**", "/js/**")
            .permitAll()
            .anyRequest()
            .authenticated())
        .formLogin(form -> form.failureHandler(authenticationFailureHandler)
            .defaultSuccessUrl("/", true)
            .loginPage("/login")
            .permitAll())
        .logout(logout ->
            logout.logoutUrl("/logout").logoutSuccessUrl("/login").invalidateHttpSession(true));

    return http.build();
  }
}
