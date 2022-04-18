package com.sbt.pprb.qa.test_task.controller.mvc;

import com.sbt.pprb.qa.test_task.VendingMachineApplication;
import com.sbt.pprb.qa.test_task.model.dto.AppUser;
import io.qameta.allure.Epic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.FormLoginRequestBuilder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import javax.servlet.http.Cookie;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest(classes = VendingMachineApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Epic("Unit-тесты фильтров запросов")
@DisplayName("Тесты фильтров запросов (SecurityConfig)")
class SecurityFiltersTest {

    @Autowired
    private MockMvc mockMvc;
    @Value("${system.rest.auth.username}")
    private String username;
    @Value("${system.rest.auth.password}")
    private String password;

    @Test
    void itShouldReturnRegisterPage() throws Exception {
        // Given
        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setEnabled(true);
        user.setAuthority("USER");

        RequestPostProcessor auth = SecurityMockMvcRequestPostProcessors.user(user);

        // When
        MockHttpServletRequestBuilder request = get("/").with(auth);

        // Then
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername(username).withRoles("USER"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(content().string(
                        allOf(
                                containsString("<title>Create new user</title>"),
                                containsString("<form class=\"form-signin\" method=\"post\" action=\"/registration\">"),
                                containsString("<h2 class=\"form-signin-heading\">Create user</h2>"),
                                containsString("<button class=\"btn btn-lg btn-primary btn-block\" type=\"submit\">Создать</button>")
                        )
                ));
    }

    @Test
    void itShouldAddHintHeaders() throws Exception {
        // Given
        FormLoginRequestBuilder request = formLogin().user("test").password("test");
        MvcResult mvcResult = mockMvc.perform(request)
                .andExpect(status().isFound())
                .andReturn();

        Cookie cookie = mvcResult.getResponse().getCookie("SESSION");
        String redirectedUrl = mvcResult.getResponse().getRedirectedUrl();
        assertThat(redirectedUrl).isNotNull();

        // When
        MockHttpServletRequestBuilder loginErrorRequest = get(redirectedUrl).cookie(cookie);

        // Then
        mockMvc.perform(loginErrorRequest)
                .andExpect(status().isOk())
                .andExpect(header().string("username", is(username)))
                .andExpect(header().string("password", is(password)))
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(content().string(containsString("<div class=\"alert alert-danger\" role=\"alert\">[401] Bad credentials: Inspect response headers of login page for credentials</div>")));
    }
}