package com.abc.telecom.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

public class JwtUtilTest {
    JwtUtil jwtUtil;

    @BeforeEach
    void setup(){
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "jwtSecret", "01234567890123456789012345678901");
        ReflectionTestUtils.setField(jwtUtil, "jwtExpirationMs", 3600000);
    }

    @Test
    void generate_and_validate_and_parse(){
        String token = jwtUtil.generateToken("alice");
        assertThat(token).isNotBlank();
        assertThat(jwtUtil.validateToken(token)).isTrue();
        assertThat(jwtUtil.getUsernameFromToken(token)).isEqualTo("alice");
    }

    @Test
    void invalid_token_returns_false(){
        assertThat(jwtUtil.validateToken("bad.token.here")).isFalse();
    }

    @Test
    void tampered_token_is_invalid(){
        String token = jwtUtil.generateToken("bob");
        assertThat(jwtUtil.validateToken(token + "x")).isFalse();
    }
}
