package com.abc.telecom.controller;

import com.abc.telecom.model.ServiceTemplate;
import com.abc.telecom.model.TelecomService;
import com.abc.telecom.service.ServiceTemplateService;
import com.abc.telecom.service.TelecomServiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ServiceTemplateControllerTest {
    ServiceTemplateService templateService;
    TelecomServiceService telecomServiceService;
    MockMvc mvc;

    @BeforeEach
    void setup() {
        templateService = Mockito.mock(ServiceTemplateService.class);
        telecomServiceService = Mockito.mock(TelecomServiceService.class);
        ServiceTemplateController controller = new ServiceTemplateController(templateService, telecomServiceService);
        mvc = org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void list_returnsTemplates() throws Exception {
        ServiceTemplate t = new ServiceTemplate(); t.setId(2L); t.setServiceName("T1");
        Mockito.when(templateService.findAll()).thenReturn(List.of(t));

        mvc.perform(get("/api/services/templates")).andExpect(status().isOk()).andExpect(jsonPath("$[0].serviceName").value("T1"));
    }

    @Test
    void create_returnsCreatedWithLocation() throws Exception {
        ServiceTemplate in = new ServiceTemplate(); in.setServiceName("NEW"); in.setMonthlyFee(4.5);
        ServiceTemplate saved = new ServiceTemplate(); saved.setId(10L); saved.setServiceName("NEW");
        Mockito.when(templateService.create(any())).thenReturn(saved);

        String json = "{ \"serviceName\": \"NEW\", \"monthlyFee\": 4.5 }";
        mvc.perform(post("/api/services/templates").contentType("application/json").content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/api/services/templates/10")))
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void assignToCustomer_createsService() throws Exception {
        ServiceTemplate t = new ServiceTemplate(); t.setId(7L); t.setServiceName("TPL"); t.setMonthlyFee(8.0);
        Mockito.when(templateService.findById(7L)).thenReturn(java.util.Optional.of(t));
        TelecomService created = new TelecomService(); created.setServiceId(77L); created.setServiceName("TPL");
        Mockito.when(telecomServiceService.create(any())).thenReturn(created);

        mvc.perform(post("/api/services/templates/7/assign?customerId=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serviceId").value(77));
    }
}
