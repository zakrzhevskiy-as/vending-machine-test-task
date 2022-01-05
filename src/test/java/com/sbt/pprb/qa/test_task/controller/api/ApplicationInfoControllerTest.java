package com.sbt.pprb.qa.test_task.controller.api;

import com.sbt.pprb.qa.test_task.CommonTestContext;
import com.sbt.pprb.qa.test_task.VendingMachineApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = VendingMachineApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class ApplicationInfoControllerTest extends CommonTestContext {

    @Autowired
    private MockMvc mvc;

    @Value("${SPRING_DB_URL:jdbc:postgresql://localhost:5432/test-task}")
    private String expectedDbUrl;
    @Value("${system.db.schema}")
    private String expectedDbSchema;
    @Value("${system.db.reader.user}")
    private String expectedDbUsername;
    @Value("${system.db.reader.password}")
    private String expectedDbPassword;
    @Value("${system.rest.auth.type}")
    private String expectedRestAuthType;

    @Test
    void dbConfig_endpoint_test() throws Exception {
        MockHttpServletRequestBuilder request = get("/api/app-info/database")
                .with(auth)
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.url", is(expectedDbUrl)))
                .andExpect(jsonPath("$.schema", is(expectedDbSchema)))
                .andExpect(jsonPath("$.username", is(expectedDbUsername)))
                .andExpect(jsonPath("$.password", is(expectedDbPassword)));
    }

    @Test
    void restConfig_endpoint_test() throws Exception {
        MockHttpServletRequestBuilder request = get("/api/app-info/rest")
                .with(auth)
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.auth_type", is(expectedRestAuthType)))
                .andExpect(jsonPath("$.credentials", is("Same as for UI")))
                .andExpect(jsonPath("$.documentation", is("swagger-ui/index.html")));
    }

    @Test
    void appInfo_endpoint_test() throws Exception {
        MockHttpServletRequestBuilder request = get("/api/app-info")
                .with(auth)
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string("{}"));
    }
}