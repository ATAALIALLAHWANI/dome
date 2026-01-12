package com.api.demo.security.manager;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.api.demo.entity.SysUserEntity;
import com.api.demo.repository.SysUserRepository;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class CustomAuthenticationManager implements AuthenticationManager {

    private final SysUserRepository userRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        // Fetch user from DB
        SysUserEntity user = userRepository.findByUserName(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Compare plain-text password (from DB) with input
        if (!user.getUserPassword().equals(password)) {
            throw new RuntimeException("Incorrect password");
        }

        // Assign roles — for simplicity, we can use ADMIN_FLAG or USER_TYPE
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (user.getAdminFlag() != null && user.getAdminFlag() == 1) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        // Return authenticated token
        return new UsernamePasswordAuthenticationToken(username, password, authorities);
    }
}
