package com.sbt.pprb.qa.test_task.controller.api;

import com.sbt.pprb.qa.test_task.controller.ControllerTestContext;
import com.sbt.pprb.qa.test_task.service.ApplicationInfoService;
import io.qameta.allure.Epic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Epic("Integration-тесты контроллеров")
@DisplayName("Тесты контроллера апи ApplicationInfoController")
class ApplicationInfoControllerTest extends ControllerTestContext {

    @MockBean
    private ApplicationInfoService applicationInfoService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void itShouldReturnDbConfig() throws Exception {
        // Given
        Map<String, String> body = new HashMap<>();
        body.put("url", "jdbc:postgresql://localhost:5432/test-task");
        body.put("schema", "test_schema");
        body.put("username", "test_username");
        body.put("password", "test_password");

        when(applicationInfoService.getDbConfig()).thenReturn(body);

        // When
        MockHttpServletRequestBuilder request = get("/api/v1/app-info/database").with(auth);

        // Then
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.url", is(body.get("url"))))
                .andExpect(jsonPath("$.schema", is(body.get("schema"))))
                .andExpect(jsonPath("$.username", is(body.get("username"))))
                .andExpect(jsonPath("$.password", is(body.get("password"))));
    }

    @Test
    void itShouldReturnUnauthorizedOnDbConfigRequest() throws Exception {
        // When
        MockHttpServletRequestBuilder request = get("/api/v1/app-info/database");

        // Then
        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    void itShouldReturnRestConfig() throws Exception {
        // Given
        Map<String, String> body = new HashMap<>();
        body.put("auth_type", "Basic");
        body.put("credentials", "test_credentials");
        body.put("documentation", "test_url");

        when(applicationInfoService.getRestConfig()).thenReturn(body);

        // When
        MockHttpServletRequestBuilder request = get("/api/v1/app-info/rest").with(auth);

        // Then
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.auth_type", is("Basic")))
                .andExpect(jsonPath("$.credentials", is("test_credentials")))
                .andExpect(jsonPath("$.documentation", is("test_url")));
    }

    @Test
    void itShouldReturnUnauthorizedOnRestConfigRequest() throws Exception {
        // When
        MockHttpServletRequestBuilder request = get("/api/v1/app-info/rest");

        // Then
        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    void itShouldReturnAppInfo() throws Exception {
        // Given
        when(applicationInfoService.getAppInfo()).thenReturn(Collections.singletonMap("username", USERNAME));

        // When
        MockHttpServletRequestBuilder request = get("/api/v1/app-info").with(auth);

        // Then
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username", is(USERNAME)));
    }

    @Test
    void itShouldReturnUnauthorizedOnAppInfoRequest() throws Exception {
        // When
        MockHttpServletRequestBuilder request = get("/api/v1/app-info");

        // Then
        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }
}