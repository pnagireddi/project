package com.abc.telecom.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GlobalExceptionHandlerTest {
    GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleGenericException_returns500() {
        ResponseEntity<ApiError> r = handler.handleException(new RuntimeException("boom"));
        assertEquals(500, r.getStatusCodeValue());
        assertEquals("boom", r.getBody().getMessage());
    }

    @Test
    void handleUserExists_returns409() {
        ResponseEntity<ApiError> r = handler.handleUserExists(new UserAlreadyExistsException("exists"));
        assertEquals(409, r.getStatusCodeValue());
        assertEquals("exists", r.getBody().getMessage());
    }

    @Test
    void handleAuthentication_returns401() {
        ResponseEntity<ApiError> r = handler.handleAuthentication(new BadCredentialsException("bad"));
        assertEquals(401, r.getStatusCodeValue());
        assertEquals("Bad credentials", r.getBody().getMessage());
    }
}
