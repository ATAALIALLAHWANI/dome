package com.api.demo.security;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class EmployeePrincipal implements UserDetails {

    private Long staffId;
    private String username;
    private Long siteId;
    private String password; // optional
    private Collection<? extends GrantedAuthority> authorities;

    public EmployeePrincipal(Long staffId, String username, Long siteId, String password,
                             Collection<? extends GrantedAuthority> authorities) {
        this.staffId = staffId;
        this.username = username;
        this.siteId = siteId;
        this.password = password;
        this.authorities = authorities;
    }

    public Long getStaffId() { return staffId; }
    public Long getSiteId() { return siteId; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() { return password; }

    @Override
    public String getUsername() { return username; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
