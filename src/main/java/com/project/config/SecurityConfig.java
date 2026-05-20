package com.project.config;

import com.project.service.CustomUserDetailsService;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final CustomUserDetailsService userDetailsService;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(
            auth ->
                auth
                    .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR)
                    .permitAll()

                    // Публичные ресурсы и эндпоинты
                    .requestMatchers(
                        "/",
                        "/auth/**",
                        "/api/v1/tours",
                        "/api/v1/excursions",
                        "/api/v1/currency/**",
                        "/favicon.ico")
                    .permitAll()
                    .requestMatchers("/static/**", "/css/**", "/js/**", "/images/**", "/webjars/**")
                    .permitAll()

                    // Доступ для Менеджеров
                    .requestMatchers(
                        "/admin/**",
                        "/tours/create",
                        "/tours/create/**",
                        "/tours/*/edit",
                        "/tours/*/edit/**",
                        "/tours/*/delete",
                        "/tours/*/delete/**")
                    .hasAnyRole("MANAGER", "ADMIN")
                    .requestMatchers(
                        "/excursions/create",
                        "/excursions/create/**",
                        "/excursions/*/edit",
                        "/excursions/*/edit/**",
                        "/excursions/*/delete",
                        "/excursions/*/delete/**")
                    .hasAnyRole("MANAGER", "ADMIN")
                    .requestMatchers(
                        "/hotels/create",
                        "/hotels/create/**",
                        "/hotels/*/edit",
                        "/hotels/*/edit/**",
                        "/hotels/*/delete",
                        "/hotels/*/delete/**")
                    .hasAnyRole("MANAGER", "ADMIN")
                    .requestMatchers("/users", "/users/*/role")
                    .hasRole("ADMIN")

                    // Все остальное требует авторизации
                    .anyRequest()
                    .authenticated())
        .formLogin(
            form ->
                form.loginPage("/auth/login")
                    .loginProcessingUrl("/auth/login")
                    .defaultSuccessUrl("/tours", true)
                    .failureUrl("/auth/login?error=true")
                    .permitAll())
        .logout(
            logout ->
                logout
                    .logoutUrl("/auth/logout")
                    .logoutSuccessUrl("/?logout=true")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .permitAll())
        .csrf(
            csrf ->
                csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .ignoringRequestMatchers("/api/**"))
        .authenticationProvider(authenticationProvider());

    return http.build();
  }

  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
  }
}
