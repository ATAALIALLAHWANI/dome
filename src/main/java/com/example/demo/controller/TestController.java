package com.example.demo.controller;

import com.example.demo.service.ExternalDBService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;

@RestController
public class TestController {

    private final ExternalDBService service;

    public TestController(ExternalDBService service) {
        this.service = service;
    }

    @GetMapping("/external-db-test")
    public String testExternalDb() {

        try (Connection con = service.getDBConnection()) {
            return "External DB Connected Successfully";
        } catch (Exception e) {
            return "External DB Connection FAILED: " + e.getMessage();
        }
    }
}
