package com.abc.telecom.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApiErrorTest {
    @Test
    void defaultAndParamConstructorAndAccessors() {
        ApiError d = new ApiError();
        d.setStatus(500);
        d.setMessage("oops");
        assertEquals(500, d.getStatus());
        assertEquals("oops", d.getMessage());

        ApiError p = new ApiError(404, "notfound");
        assertEquals(404, p.getStatus());
        assertEquals("notfound", p.getMessage());
    }
}
