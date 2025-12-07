package com.abc.telecom.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import java.lang.reflect.Method;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class WebConfigTest {
    @Test
    void addCorsMappings_creates_expected_configuration() throws Exception {
        WebConfig webConfig = new WebConfig();
        CorsRegistry registry = new CorsRegistry();
        webConfig.addCorsMappings(registry);

        Method m = CorsRegistry.class.getDeclaredMethod("getCorsConfigurations");
        m.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, CorsConfiguration> configs = (Map<String, CorsConfiguration>) m.invoke(registry);

        assertThat(configs).containsKey("/api/**");

        CorsConfiguration cfg = configs.get("/api/**");
        assertThat(cfg.getAllowedOrigins()).contains("http://localhost:3000");
        assertThat(cfg.getAllowedMethods()).contains("GET", "POST", "PUT", "DELETE", "OPTIONS");
        assertThat(cfg.getAllowedHeaders()).contains("*");
        assertThat(cfg.getAllowCredentials()).isTrue();
    }
}
