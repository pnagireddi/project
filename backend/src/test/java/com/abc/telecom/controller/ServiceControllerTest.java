package com.abc.telecom.controller;

import com.abc.telecom.model.TelecomService;
import com.abc.telecom.service.TelecomServiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ServiceControllerTest {
    TelecomServiceService serviceService;
    MockMvc mvc;

    @BeforeEach
    void setup() {
        serviceService = Mockito.mock(TelecomServiceService.class);
        ServiceController controller = new ServiceController(serviceService);
        mvc = org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void updateService_whenFound_returnsUpdated() throws Exception {
        TelecomService existing = new TelecomService(); existing.setServiceId(5L); existing.setServiceName("Old");
        when(serviceService.findById(5L)).thenReturn(Optional.of(existing));
        TelecomService updated = new TelecomService(); updated.setServiceId(5L); updated.setServiceName("NewName");
        when(serviceService.update(any())).thenReturn(updated);

        String body = "{ \"serviceName\": \"NewName\", \"monthlyFee\": 15.5 }";
        mvc.perform(put("/api/services/5").contentType("application/json").content(body)
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serviceId").value(5))
                .andExpect(jsonPath("$.serviceName").value("NewName"));
    }

    @Test
    void updateService_whenNotFound_returns404() throws Exception {
        when(serviceService.findById(7L)).thenReturn(Optional.empty());
        String body = "{ \"serviceName\": \"X\" }";
        mvc.perform(put("/api/services/7").contentType("application/json").content(body)
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteService_callsDeleteAndReturnsNoContent() throws Exception {
        mvc.perform(delete("/api/services/9")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNoContent());

        verify(serviceService).delete(9L);
    }
}
