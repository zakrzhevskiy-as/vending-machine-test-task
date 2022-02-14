package com.sbt.pprb.qa.test_task;

import com.sbt.pprb.qa.test_task.model.dto.AppUser;
import com.sbt.pprb.qa.test_task.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@TestPropertySource(locations = "classpath:application-integrationtest.yml")
public class CommonTestContext {

    protected static final String username = "test_user";
    protected static RequestPostProcessor auth;

    @BeforeAll
    static void precondition(@Autowired UserService userService) {
        AppUser user = userService.createUser(username, "password");
        auth = SecurityMockMvcRequestPostProcessors.user(user);
    }
}
