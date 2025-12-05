package com.abc.telecom.config;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ABC Telecom - Postpaid Billing System API")
                        .version("v1")
                        .description("API documentation for ABC Telecom Postpaid Billing System")
                        .contact(new Contact().name("ABC Telecom Dev Team").email("devops@abctelecom.example"))
                        .license(new License().name("MIT").url("https://opensource.org/licenses/MIT"))
                );
    }
}
