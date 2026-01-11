package com.api.demo.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(info = @Info(title = "SIQ External DB API", description = "APIs for testing and accessing external database connections", version = "1.0.0", contact = @Contact(name = "SIQ API Support", email = "support@siq.com")), servers = {
        @Server(url = "http://localhost:9090", description = "Docker Server"),
        @Server(url = "http://localhost:8080", description = "Local Server"),
        @Server(url = "http://194.165.140.205:9090", description = "API Server")

})
public class SwaggerConfig {
}
