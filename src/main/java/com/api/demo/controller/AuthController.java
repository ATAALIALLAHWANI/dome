package com.api.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import com.api.demo.dto.EmployeeLoginResponseDto;
import com.api.demo.dto.EmployeeLoginWithTokenDto;
import com.api.demo.dto.LoginRequestDto;
import com.api.demo.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")  // Base URL
@CrossOrigin(origins = "*")
public class AuthController {
    @Autowired
    private  AuthService authService;

 @PostMapping("/employee-login")
    @Operation(
        summary = "Employee Login",
        description = "Authenticate user and return JWT token with employee info"
    )
    public void login(@RequestBody LoginRequestDto dto) {
        // ⚠ DO NOT IMPLEMENT LOGIC HERE
        // This method exists ONLY for Swagger documentation
        throw new UnsupportedOperationException("Handled by Spring Security filter");
    }


    
  

}



//      @PostMapping("/employee-login")
// public EmployeeLoginWithTokenDto login(@RequestBody LoginRequestDto request, HttpServletRequest httpRequest) {
//     // The token will be set by your AuthenticationFilter in header
//     String token = httpRequest.getHeader("Authorization");
//     return authService.EmployeeLogin(request.getUsername(), request.getPassword(), token);
// }