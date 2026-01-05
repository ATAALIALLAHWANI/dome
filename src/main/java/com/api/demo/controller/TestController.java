package com.api.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.demo.service.ExternalDBService;

import java.sql.Connection;

@RestController
@Tag(name = "Database Test API", description = "APIs for testing external database connectivity")
public class TestController {

    private final ExternalDBService service;

    public TestController(ExternalDBService service) {
        this.service = service;
    }

    @Operation(
        summary = "Test External Database Connection",
        description = "Attempts to connect to the external Oracle database using configured properties"
    )
    @GetMapping("/external-db-test")
    public String testExternalDb() {

        try (Connection con = service.getDBConnection()) {
            return "External DB Connected Successfully";
        } catch (Exception e) {
            return "External DB Connection FAILED: " + e.getMessage();
        }
    }
}
