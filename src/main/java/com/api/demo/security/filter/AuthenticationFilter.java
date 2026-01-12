package com.api.demo.security.filter;

import java.io.IOException;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.api.demo.dto.EmployeeLoginResponseDto;
import com.api.demo.dto.EmployeeLoginWithTokenDto;
import com.api.demo.dto.LoginRequestDto;
import com.api.demo.security.SecurityConstants;
import com.api.demo.security.manager.CustomAuthenticationManager;
import com.api.demo.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

      private final CustomAuthenticationManager authenticationManager;
    private final AuthService authService;

    public AuthenticationFilter(CustomAuthenticationManager authenticationManager,
                                AuthService authService) {
        this.authenticationManager = authenticationManager;
        this.authService = authService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        try {
            LoginRequestDto loginRequest = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(), loginRequest.getPassword());

            return authenticationManager.authenticate(authentication);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

   @Override
protected void successfulAuthentication(HttpServletRequest request,
                                        HttpServletResponse response,
                                        FilterChain chain,
                                        Authentication authResult)
        throws IOException {

    // 1️⃣ Create JWT
    String token = JWT.create()
            .withSubject(authResult.getName())
            .withClaim("roles", authResult.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()))
            .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.TOKEN_EXPIRATION))
            .sign(Algorithm.HMAC512(SecurityConstants.SECRET_KEY));

    // 2️⃣ Load employee info
    EmployeeLoginResponseDto employee =
            authService.loadEmployeeByUsername(authResult.getName());

    // 3️⃣ Create response DTO
    EmployeeLoginWithTokenDto loginResponse = new EmployeeLoginWithTokenDto();
    loginResponse.setToken(token);
    loginResponse.setEmployee(employee);

    // 4️⃣ Return JSON response
    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType("application/json");
    new ObjectMapper().writeValue(response.getOutputStream(), loginResponse);
}

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(failed.getMessage());
        response.getWriter().flush();
    }
}
