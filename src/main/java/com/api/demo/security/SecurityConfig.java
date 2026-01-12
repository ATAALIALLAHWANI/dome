package com.api.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;

import com.api.demo.security.filter.AuthenticationFilter;
import com.api.demo.security.filter.ExceptionHandlerFilter;
import com.api.demo.security.filter.JWTAuthorizationFilter;
import com.api.demo.security.filter.CustomAccessDeniedHandler;
import com.api.demo.security.manager.CustomAuthenticationManager;
import com.api.demo.service.AuthService;

import lombok.AllArgsConstructor;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
@AllArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationManager customAuthenticationManager;
     private final AuthService authService;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // 🔐 JWT Authentication Filter (LOGIN)
       AuthenticationFilter authenticationFilter =
        new AuthenticationFilter(customAuthenticationManager, authService);

        authenticationFilter.setFilterProcessesUrl(SecurityConstants.LOGIN_PATH);

        // 🌐 CORS
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.addAllowedOrigin("*");
        corsConfig.addAllowedMethod("*");
        corsConfig.addAllowedHeader("*");

        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(request -> corsConfig))

            .authorizeHttpRequests(auth -> auth

                // ✅ Swagger (VERY IMPORTANT)
                .requestMatchers(
                    "/swagger-ui/**",
                    "/v3/api-docs/**"
                ).permitAll()

                // ✅ Login
                .requestMatchers(HttpMethod.POST, SecurityConstants.LOGIN_PATH).permitAll()

                // 🔒 Everything else secured
                .anyRequest().authenticated()
            )

            .exceptionHandling(ex -> ex
                .accessDeniedHandler(accessDeniedHandler())
            )

            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // 🧱 Filters order
            .addFilterBefore(new ExceptionHandlerFilter(), AuthenticationFilter.class)
            .addFilter(authenticationFilter)
            .addFilterAfter(new JWTAuthorizationFilter(), AuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }
}
