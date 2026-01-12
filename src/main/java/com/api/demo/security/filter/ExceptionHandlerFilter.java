package com.api.demo.security.filter;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.api.demo.exception.UserNotFoundException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ExceptionHandlerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // Continue the filter chain
            filterChain.doFilter(request, response);

        } catch (UserNotFoundException e) {
            // User not found
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setContentType("application/json");
            response.getWriter().write("{\"status\":404, \"message\":\"User not found\"}");
            response.getWriter().flush();

        } catch (JWTVerificationException e) {
            // Invalid JWT
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"status\":401, \"message\":\"Invalid or expired JWT token\"}");
            response.getWriter().flush();

        } catch (RuntimeException e) {
            // Other runtime exceptions
            e.printStackTrace(); // Optional: log for debugging
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"status\":400, \"message\":\"Bad request\"}");
            response.getWriter().flush();
        }
    }
}
