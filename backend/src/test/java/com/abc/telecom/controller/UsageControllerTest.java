package com.abc.telecom.controller;

import com.abc.telecom.model.UsageRecord;
import com.abc.telecom.service.UsageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UsageControllerTest {
    UsageService usageService;
    MockMvc mvc;

    @BeforeEach
    void setup() {
        usageService = Mockito.mock(UsageService.class);
        UsageController controller = new UsageController(usageService);
        mvc = org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getUsage_returnsList() throws Exception {
        UsageRecord r = new UsageRecord(); r.setUsageId(5L); r.setUsageAmount(12.3);
        when(usageService.findByServiceId(10L)).thenReturn(List.of(r));

        mvc.perform(get("/api/services/10/usage")).andExpect(status().isOk()).andExpect(jsonPath("$[0].usageId").value(5));
    }

    @Test
    void addUsage_createsAndReturnsLocation() throws Exception {
        UsageRecord r = new UsageRecord(); r.setUsageId(7L); r.setUsageAmount(3.3);
        when(usageService.create(any())).thenReturn(r);

        String body = "{ \"usageAmount\": 3.3, \"unit\": \"MB\" }";
        mvc.perform(post("/api/services/12/usage").contentType("application/json").content(body)
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/api/services/12/usage/7")))
                .andExpect(jsonPath("$.usageId").value(7));
    }
}
