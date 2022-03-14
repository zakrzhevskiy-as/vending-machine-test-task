package com.sbt.pprb.qa.test_task.service;

import com.sbt.pprb.qa.test_task.model.dto.AppUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class ApplicationInfoServiceTest {

    private ApplicationInfoService underTest;

    @BeforeEach
    void setUp() {
        underTest = new ApplicationInfoService();
        ReflectionTestUtils.setField(underTest, "jdbcUrl", "jdbcUrlTestValue");
        ReflectionTestUtils.setField(underTest, "schema", "schemaTestValue");
        ReflectionTestUtils.setField(underTest, "username", "usernameTestValue");
        ReflectionTestUtils.setField(underTest, "password", "passwordTestValue");
        ReflectionTestUtils.setField(underTest, "authType", "authTypeTestValue");
    }

    @Test
    void canGetDbConfig() {
        // when
        Map<String, String> result = underTest.getDbConfig();

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).hasFieldOrPropertyWithValue("url", "jdbcUrlTestValue");
        assertThat(result).hasFieldOrPropertyWithValue("schema", "schemaTestValue");
        assertThat(result).hasFieldOrPropertyWithValue("username", "usernameTestValue");
        assertThat(result).hasFieldOrPropertyWithValue("password", "passwordTestValue");
    }

    @Test
    void canGetRestConfig() {
        // when
        Map<String, String> result = underTest.getRestConfig();

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).hasFieldOrPropertyWithValue("auth_type", "authTypeTestValue");
        assertThat(result).hasFieldOrPropertyWithValue("credentials", "Same as for UI");
        assertThat(result).hasFieldOrPropertyWithValue("documentation", "swagger-ui/index.html");
    }

    @Test
    void canGetAppInfo() {
        // given
        AppUser user = new AppUser();
        user.setUsername("test_user");
        user.setPassword("password");
        user.setEnabled(true);
        user.setAuthority("USER");
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // when
        Map<String, String> result = underTest.getAppInfo();

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).hasFieldOrPropertyWithValue("username", "test_user");
    }
}