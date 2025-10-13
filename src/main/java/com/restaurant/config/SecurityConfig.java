package com.restaurant.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(
            @Qualifier("customUserDetailsService") UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions().disable())
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(authz -> authz
                        // Publicly accessible pages and resources
                        .requestMatchers(new AntPathRequestMatcher("/")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/index.html")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/login")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/register")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/register.html")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/css/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/js/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/images/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/assets/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/webjars/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/menu")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/menu/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/orders/new")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/orders/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/reservations")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/reservations/guest")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/reservations/group")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/reservations/check-availability")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/reservations/time-slots")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/reservations/alternatives")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/payment-test/**")).permitAll() // Allow payment test endpoints
                        .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
                        // Dashboard and home require authentication
                        .requestMatchers(new AntPathRequestMatcher("/dashboard")).authenticated()
                        .requestMatchers(new AntPathRequestMatcher("/dashboard.html")).authenticated()
                        .requestMatchers(new AntPathRequestMatcher("/home")).authenticated()
                        // Manager dashboard requires authentication
                        .requestMatchers(new AntPathRequestMatcher("/manager/**")).authenticated()
                        // Schedule management requires authentication
                        .requestMatchers(new AntPathRequestMatcher("/schedule/**")).authenticated()
                        // Event booking requires authentication
                        .requestMatchers(new AntPathRequestMatcher("/events/**")).authenticated()
                        // Kitchen management requires authentication
                        .requestMatchers(new AntPathRequestMatcher("/kitchen/**")).authenticated()
                        // Chef dashboard requires authentication
                        .requestMatchers(new AntPathRequestMatcher("/chef/**")).authenticated()
                        // Admin dashboard requires authentication
                        .requestMatchers(new AntPathRequestMatcher("/admin/**")).authenticated()
                        // Banquet hall management requires authentication
                        .requestMatchers(new AntPathRequestMatcher("/banquet/**")).authenticated()
                        // Customer relations management requires authentication
                        .requestMatchers(new AntPathRequestMatcher("/customer-relations/**")).authenticated()
                        // Member reservations require authentication
                        .requestMatchers(new AntPathRequestMatcher("/reservations/new")).authenticated()
                        .requestMatchers(new AntPathRequestMatcher("/reservations/confirmation/**")).authenticated()
                        .requestMatchers(new AntPathRequestMatcher("/reservations/payment/**")).authenticated()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")            // Custom login page URL
                        .loginProcessingUrl("/login")   // POST login processing URL
                        .defaultSuccessUrl("/dashboard")  // Redirect to dashboard after successful login
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }
}
