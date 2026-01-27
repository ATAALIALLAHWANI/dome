package com.api.demo.security.filter;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.api.demo.dto.EmployeeLoginResponseDto;
import com.api.demo.dto.EmployeeLoginWithTokenDto;
import com.api.demo.dto.LoginRequestDto;
import com.api.demo.security.EmployeePrincipal;
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
            LoginRequestDto loginRequest = new ObjectMapper().readValue(request.getInputStream(),
                    LoginRequestDto.class);

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

        EmployeeLoginResponseDto empDto = authService.loadEmployeeByUsername(authResult.getName());

        EmployeePrincipal employeePrincipal = new EmployeePrincipal(
                empDto.getStaffId(),
                empDto.getEmpNameEng(),
                empDto.getSiteId(),
                empDto.getEmpType(),
                null,
                authResult.getAuthorities());

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                employeePrincipal, null, employeePrincipal.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

       String token = JWT.create()
        .withSubject(employeePrincipal.getUsername())
        .withClaim(
            "roles",
            employeePrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList())
        )
        .withClaim("siteId", employeePrincipal.getSiteId())
        .withClaim("empType", employeePrincipal.getEmpType()) // 👈 ADD
        .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.TOKEN_EXPIRATION))
        .sign(Algorithm.HMAC512(SecurityConstants.SECRET_KEY));

        // Prepare response
        EmployeeLoginWithTokenDto loginResponse = new EmployeeLoginWithTokenDto();
        loginResponse.setToken(token);
        loginResponse.setEmployee(empDto);

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
