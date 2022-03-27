package com.sbt.pprb.qa.test_task.controller.mvc;

import com.sbt.pprb.qa.test_task.controller.ControllerTestContext;
import com.sbt.pprb.qa.test_task.model.dto.AppUser;
import com.sbt.pprb.qa.test_task.model.exception.UsernameAlreadyTakenException;
import com.sbt.pprb.qa.test_task.service.UserService;
import io.qameta.allure.Epic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Epic("Integration-тесты контроллеров")
@DisplayName("Тесты контроллера RegisterController")
class RegisterControllerTest extends ControllerTestContext {

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void itShouldReturnRegisterPage() throws Exception {
        // When
        MockHttpServletRequestBuilder request = get("/registration").with(auth);

        // Then
        mockMvc.perform(request)
                .andExpect(status().isOk())
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
    void itShouldReturnUnauthorizedWhenRequestRegisterPage() throws Exception {
        // When
        MockHttpServletRequestBuilder request = get("/registration");

        // Then
        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    void itShouldCreateNewUser() throws Exception {
        // Given
        AppUser user = new AppUser();
        user.setId(1L);
        user.setUsername(USERNAME);
        user.setPassword("password");
        user.setAuthority("USER");
        user.setEnabled(true);
        user.setCreated(LocalDateTime.now());
        user.setModified(LocalDateTime.now());

        when(userService.createUser(any())).thenReturn(user);

        // When
        MockHttpServletRequestBuilder request = post("/registration")
                .with(auth)
                .param("username", USERNAME)
                .param("password", "password");

        // Then
        mockMvc.perform(request)
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("login"));
    }

    @Test
    void itShouldThrowUsernameAlreadyTakenException() throws Exception {
        // Given
        doThrow(new UsernameAlreadyTakenException(USERNAME)).when(userService).createUser(any());

        // When
        MockHttpServletRequestBuilder request = post("/registration")
                .with(auth)
                .param("username", USERNAME)
                .param("password", "password");

        // Then
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(content().string(
                        allOf(
                                containsString("<title>Create new user</title>"),
                                containsString("<form class=\"form-signin\" method=\"post\" action=\"/registration\">"),
                                containsString("<div class=\"alert alert-danger\" role=\"alert\">Username &#39;" + USERNAME + "&#39; already taken</div>"),
                                containsString("<h2 class=\"form-signin-heading\">Create user</h2>"),
                                containsString("<button class=\"btn btn-lg btn-primary btn-block\" type=\"submit\">Создать</button>")
                        )
                ));
    }
}