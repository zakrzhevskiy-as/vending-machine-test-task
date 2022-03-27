package com.sbt.pprb.qa.test_task.controller.mvc;

import com.sbt.pprb.qa.test_task.controller.ControllerTestContext;
import io.qameta.allure.Epic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Epic("Integration-тесты контроллеров")
@DisplayName("Тесты контроллера HomeController")
class HomeControllerTest extends ControllerTestContext {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void itShouldReturnMainPage() throws Exception {
        // When
        MockHttpServletRequestBuilder request = get("/").with(auth);

        // Then
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(content().string(
                        allOf(
                                containsString("<title>QA Test Task: Vending Machine</title>"),
                                containsString("<link rel=\"icon\" type=\"image/ico\" href=\"favicon.ico\">"),
                                containsString("<div id=\"root\"></div>"),
                                containsString("<script src=\"/bundle.js\"></script>")
                        )
                ));
    }
}