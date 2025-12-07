package com.abc.telecom.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OpenApiConfigTest {
    @Test
    void customOpenAPI_contains_expected_info() {
        OpenApiConfig cfg = new OpenApiConfig();
        OpenAPI openAPI = cfg.customOpenAPI();

        assertThat(openAPI).isNotNull();
        Info info = openAPI.getInfo();
        assertThat(info.getTitle()).isEqualTo("ABC Telecom - Postpaid Billing System API");
        assertThat(info.getVersion()).isEqualTo("v1");
        assertThat(info.getContact().getEmail()).isEqualTo("devops@abctelecom.example");
        assertThat(info.getLicense().getName()).isEqualTo("MIT");
    }
}
