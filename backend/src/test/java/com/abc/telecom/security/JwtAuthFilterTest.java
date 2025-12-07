package com.abc.telecom.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class JwtAuthFilterTest {
    @Mock
    JwtUtil jwtUtil;
    @Mock
    CustomUserDetailsService userDetailsService;
    @InjectMocks
    JwtAuthFilter filter;

    @BeforeEach
    void before(){ SecurityContextHolder.clearContext(); }

    @Test
    void doFilterInternal_allows_valid_token() throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        when(req.getHeader("Authorization")).thenReturn("Bearer good.token");
        when(jwtUtil.validateToken("good.token")).thenReturn(true);
        when(jwtUtil.getUsernameFromToken("good.token")).thenReturn("alice");
        when(userDetailsService.loadUserByUsername("alice")).thenReturn(new org.springframework.security.core.userdetails.User("alice","x", java.util.List.of()));

        filter.doFilterInternal(req, res, chain);

        verify(chain).doFilter(req, res);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
    }

    @Test
    void doFilterInternal_no_header_leaves_context_empty() throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        when(req.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(req, res, chain);

        verify(chain).doFilter(req, res);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}
