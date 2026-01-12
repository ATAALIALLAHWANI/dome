package com.api.demo.security;

public class SecurityConstants {
    
    // Login URL for JWT authentication
    public static final String LOGIN_PATH = "/api/auth/employee-login";

    // JWT Secret Key (for signing tokens)
    public static final String SECRET_KEY = "mysecretkey123";

    // Token expiration in milliseconds (e.g., 24 hours)
    public static final long TOKEN_EXPIRATION = 24 * 60 * 60 * 1000;

    // JWT Authorization header prefix
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";
}
