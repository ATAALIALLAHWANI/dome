package com.api.demo.security.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.api.demo.dto.UserLoginResultProjection;
import com.api.demo.entity.SysUserEntity;
import com.api.demo.repository.SysUserRepository;
import com.api.demo.security.EmployeePrincipal;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class CustomAuthenticationManager implements AuthenticationManager {

    private final SysUserRepository userRepository;

    @Override
    public Authentication authenticate(Authentication authentication) {

        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        UserLoginResultProjection user = userRepository
                .login(username, password)
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (user == null) {
            throw new RuntimeException("User not found or inactive");
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        Set<Long> PATIENT_CREATOR_TYPES = Set.of(9L, 11L, 3L, 10L, 7L);

        if (user.getEmpType() != null && PATIENT_CREATOR_TYPES.contains(user.getEmpType())) {
            authorities.add(new SimpleGrantedAuthority("PATIENT_CREATE"));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        EmployeePrincipal principal = new EmployeePrincipal(
                user.getStaffId(),
                user.getUserName(),
                user.getSiteId(),
                user.getEmpType(),
                null,
                authorities);

        return new UsernamePasswordAuthenticationToken(principal, null, authorities);
    }

}
