package com.abc.telecom.config;

import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ModelPropertiesTest {
    @Test
    void applicationProperties_hasModelEnabled() throws Exception {
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties")) {
            Properties p = new Properties();
            assertNotNull(in, "application.properties not found on classpath");
            p.load(in);
            assertEquals("claude-sonnet-4.5", p.getProperty("ai.default-model"));
            assertEquals("true", p.getProperty("ai.enable-for-all-clients"));
        }
    }
}
