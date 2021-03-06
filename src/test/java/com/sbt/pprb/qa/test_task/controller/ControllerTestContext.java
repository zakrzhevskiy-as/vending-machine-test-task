package com.sbt.pprb.qa.test_task.controller;

import com.sbt.pprb.qa.test_task.VendingMachineApplication;
import com.sbt.pprb.qa.test_task.model.dto.AppUser;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@AutoConfigureMockMvc
@SpringBootTest(classes = VendingMachineApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class ControllerTestContext {

    protected static final String USERNAME = "test_user";
    protected static RequestPostProcessor auth;

    @BeforeAll
    static void precondition(@Autowired PasswordEncoder passwordEncoder) {
        AppUser user = new AppUser();
        user.setUsername(USERNAME);
        user.setPassword(passwordEncoder.encode("password"));
        user.setEnabled(true);
        user.setAuthority("USER");

        auth = SecurityMockMvcRequestPostProcessors.user(user);
    }
}
