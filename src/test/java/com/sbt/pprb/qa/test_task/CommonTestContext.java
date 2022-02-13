package com.sbt.pprb.qa.test_task;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestPropertySource;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@TestPropertySource(locations = "classpath:application-integrationtest.yml")
public class CommonTestContext {

    protected static SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor auth;

    @BeforeAll
    static void precondition() {
        auth = user("qa_engineer").password("vending_machine").roles("USER");
    }
}
