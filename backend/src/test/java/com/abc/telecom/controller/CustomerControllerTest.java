package com.abc.telecom.controller;

import com.abc.telecom.model.Customer;
import com.abc.telecom.model.TelecomService;
import com.abc.telecom.model.ServiceTemplate;
import com.abc.telecom.service.CustomerService;
import com.abc.telecom.service.TelecomServiceService;
import com.abc.telecom.service.ServiceTemplateService;
import com.abc.telecom.security.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CustomerControllerTest {
    CustomerService customerService;
    TelecomServiceService serviceService;
    AuthService authService;
    ServiceTemplateService templateService;

    MockMvc mvc;
    ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        customerService = Mockito.mock(CustomerService.class);
        serviceService = Mockito.mock(TelecomServiceService.class);
        authService = Mockito.mock(AuthService.class);
        templateService = Mockito.mock(ServiceTemplateService.class);

        CustomerController controller = new CustomerController(customerService, serviceService, authService, templateService);
        mvc = org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getCustomer_returnsProfileWhenFound() throws Exception {
        Customer c = new Customer(); c.setCustomerId(11L); c.setFullName("Alice");
        Mockito.when(customerService.findById(1L)).thenReturn(Optional.of(c));

        mvc.perform(get("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(11));
    }

    @Test
    void getCustomer_returnsNotFoundWhenMissing() throws Exception {
        Mockito.when(customerService.findById(2L)).thenReturn(Optional.empty());
        mvc.perform(get("/api/customers/2")).andExpect(status().isNotFound());
    }

    @Test
    void getServices_returnsList() throws Exception {
        TelecomService s = new TelecomService(); s.setServiceId(101L); s.setServiceName("UNLIMITED");
        Mockito.when(serviceService.findByCustomerId(5L)).thenReturn(List.of(s));

        mvc.perform(get("/api/customers/5/services"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].serviceName").value("UNLIMITED"));
    }

    @Test
    void createService_returnsCreated() throws Exception {
        TelecomService in = new TelecomService(); in.setServiceName("BASIC"); in.setMonthlyFee(9.99);
        TelecomService created = new TelecomService(); created.setServiceId(55L); created.setServiceName("BASIC");
        Mockito.when(serviceService.create(any())).thenReturn(created);

        String json = mapper.writeValueAsString(in);
        mvc.perform(post("/api/customers/3/services").contentType("application/json").content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/api/services/55")))
                .andExpect(jsonPath("$.serviceId").value(55));
    }

    @Test
    void getMyServices_noCustomer_returns404() throws Exception {
        Mockito.when(authService.getCurrentCustomerId()).thenReturn(Optional.empty());
        mvc.perform(get("/api/customers/me/services")).andExpect(status().isNotFound());
    }

    @Test
    void getMyServices_returnsListWhenPresent() throws Exception {
        Mockito.when(authService.getCurrentCustomerId()).thenReturn(Optional.of(7L));
        TelecomService s = new TelecomService(); s.setServiceId(201L); s.setServiceName("PREMIUM");
        Mockito.when(serviceService.findByCustomerId(7L)).thenReturn(List.of(s));

        mvc.perform(get("/api/customers/me/services"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].serviceName").value("PREMIUM"));
    }

    @Test
    void subscribe_missingTemplateId_returnsBadRequest() throws Exception {
        Mockito.when(authService.getCurrentCustomerId()).thenReturn(Optional.of(8L));
        mvc.perform(post("/api/customers/me/services/subscribe").contentType("application/json").content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("templateId required")));
    }

    @Test
    void subscribe_success_createsServiceFromTemplate() throws Exception {
        Mockito.when(authService.getCurrentCustomerId()).thenReturn(Optional.of(9L));
        ServiceTemplate tpl = new ServiceTemplate(); tpl.setId(5L); tpl.setServiceName("TEMPLATE_A"); tpl.setMonthlyFee(12.5);
        Mockito.when(templateService.findById(5L)).thenReturn(Optional.of(tpl));
        TelecomService created = new TelecomService(); created.setServiceId(333L); created.setServiceName("TEMPLATE_A");
        Mockito.when(serviceService.create(any())).thenReturn(created);

        String body = "{\"templateId\":5}";
        mvc.perform(post("/api/customers/me/services/subscribe").contentType("application/json").content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serviceId").value(333))
                .andExpect(jsonPath("$.serviceName").value("TEMPLATE_A"));
    }
}

