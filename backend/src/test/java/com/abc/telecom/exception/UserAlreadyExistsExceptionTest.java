package com.abc.telecom.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserAlreadyExistsExceptionTest {
    @Test
    void messageIsPreserved() {
        UserAlreadyExistsException ex = new UserAlreadyExistsException("user exists");
        assertEquals("user exists", ex.getMessage());
        assertTrue(ex instanceof RuntimeException);
    }
}
