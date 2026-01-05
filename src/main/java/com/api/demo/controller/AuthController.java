package com.api.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.api.demo.model.LoginRequestDto;
import com.api.demo.model.LoginResponseDto;
import com.api.demo.service.AuthService;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")  // Base URL
@CrossOrigin(origins = "*")
public class AuthController {
    @Autowired
    private  AuthService authService;

    // POST /api/auth/login
   @PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginRequestDto request) {
    try {
        LoginResponseDto response = authService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(response); // 200 OK
    } catch (ResponseStatusException ex) {
        // Return proper status + JSON message
        return ResponseEntity.status(ex.getStatusCode())
                             .body(Map.of("message", ex.getReason()));
    }
}


}
