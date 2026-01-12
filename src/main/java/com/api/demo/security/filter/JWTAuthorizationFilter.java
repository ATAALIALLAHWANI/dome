package com.api.demo.security.filter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.api.demo.security.SecurityConstants;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTAuthorizationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader(SecurityConstants.AUTHORIZATION);

        // If there is no Authorization header or it's not a Bearer token, continue
        if (header == null || !header.startsWith(SecurityConstants.BEARER)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract the token
        String token = header.replace(SecurityConstants.BEARER, "").trim();

        try {
            // Verify the JWT token
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(SecurityConstants.SECRET_KEY))
                    .build()
                    .verify(token);

            // Extract username/email from token
            String username = decodedJWT.getSubject();

            // Extract roles from token
            List<String> roles = decodedJWT.getClaim("roles").asList(String.class);

            // Convert roles to GrantedAuthority list
            List<GrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            // Create authentication object and set it in SecurityContext
            Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (JWTVerificationException e) {
            // If token is invalid or expired, return 401 Unauthorized
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"status\":401,\"message\":\"Invalid or expired JWT token\"}");
            response.getWriter().flush();
            return; // Stop further processing
        }

        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }
}
