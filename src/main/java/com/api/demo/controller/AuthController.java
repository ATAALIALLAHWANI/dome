package com.api.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.api.demo.dto.EmployeeLoginResponseDto;
import com.api.demo.dto.LoginRequestDto;
import com.api.demo.dto.LoginResponseDto;
import com.api.demo.dto.UserLoginResultProjection;
import com.api.demo.service.AuthService;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")  // Base URL
@CrossOrigin(origins = "*")
public class AuthController {
    @Autowired
    private  AuthService authService;
 
//     // POST /api/auth/login
//    @PostMapping("/login")
// public ResponseEntity<?> login(@RequestBody LoginRequestDto request) {
//     try {
//         LoginResponseDto response = authService.login(request.getUsername(), request.getPassword());
//         return ResponseEntity.ok(response); // 200 OK
//     } catch (ResponseStatusException ex) {
//         return ResponseEntity.status(ex.getStatusCode())
//                              .body(Map.of("message", ex.getReason()));
//     }
// }



@PostMapping("/employee-login")
public ResponseEntity<EmployeeLoginResponseDto> login(
        @RequestBody LoginRequestDto request) {
    try {
        EmployeeLoginResponseDto response = authService.EmployeeLogin(
                request.getUsername(), request.getPassword());
        return ResponseEntity.ok(response);
    } catch (ResponseStatusException ex) {
        // Return HTTP status with empty body
        return ResponseEntity.status(ex.getStatusCode()).body(null);
    }
}




 


//  // New API to insert into ALAA_TEST

// @Autowired
//     private JdbcTemplate jdbcTemplate; // Now it will resolve

//     @PostMapping("/alaa-test")
//     public ResponseEntity<?> addAlaaTest(@RequestParam String x) {
//         try {
//             String sql = "INSERT INTO IKHEALTH.ALAA_TEST (X) VALUES (?)";
//             jdbcTemplate.update(sql, x);
//             return ResponseEntity.ok(Map.of("message", "Inserted successfully", "value", x));
//         } catch (Exception e) {
//             return ResponseEntity.status(500)
//                     .body(Map.of("message", "Insert failed", "error", e.getMessage()));
//         }
//     }

}
