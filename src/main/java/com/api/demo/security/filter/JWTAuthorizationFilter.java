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
import com.api.demo.security.EmployeePrincipal;
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

        // Skip if no Bearer token
        if (header == null || !header.startsWith(SecurityConstants.BEARER)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = header.replace(SecurityConstants.BEARER, "").trim();

            // Verify JWT
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(SecurityConstants.SECRET_KEY))
                    .build()
                    .verify(token);

            // Extract username
            String username = decodedJWT.getSubject();

            // Extract siteId from token
            Long siteId = decodedJWT.getClaim("siteId").asLong();
            // Extract empType from token

            Long empType = decodedJWT.getClaim("empType").asLong();

            // Extract roles and convert to GrantedAuthority
            List<GrantedAuthority> authorities = decodedJWT.getClaim("roles").asList(String.class)
                    .stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            // Build EmployeePrincipal
            EmployeePrincipal principal = new EmployeePrincipal(
                    null, // staffId (optional, include if stored in JWT)
                    username,
                    siteId,
                    empType, 
                    null, // password not needed here
                    authorities
            );

            // Create Authentication object
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(principal, null, authorities);

            // Set in SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (JWTVerificationException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"status\":401,\"message\":\"Invalid or expired JWT token\"}");
            response.getWriter().flush();
            return;
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}
