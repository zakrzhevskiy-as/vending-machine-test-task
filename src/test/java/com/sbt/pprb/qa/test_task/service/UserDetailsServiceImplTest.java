package com.sbt.pprb.qa.test_task.service;

import com.sbt.pprb.qa.test_task.config.UserDetailsServiceImpl;
import com.sbt.pprb.qa.test_task.model.dto.AppUser;
import com.sbt.pprb.qa.test_task.repository.UsersRepository;
import io.qameta.allure.Epic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Epic("Unit-тесты сервисов")
@DisplayName("Тесты сервиса UserDetailsServiceImpl")
class UserDetailsServiceImplTest {

    @Mock
    private UsersRepository usersRepository;

    private UserDetailsServiceImpl underTest;

    @BeforeEach
    void setUp() {
        this.underTest = new UserDetailsServiceImpl(usersRepository);
    }

    @Test
    void itShouldReturnUserDetails() {
        // Given
        AppUser user = new AppUser();
        user.setId(1L);
        user.setUsername("test");
        user.setPassword("password");
        user.setEnabled(true);
        user.setAuthority("USER");
        user.setCreated(LocalDateTime.now());
        user.setModified(LocalDateTime.now());

        when(usersRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        // When
        underTest.loadUserByUsername("username");

        // Then
        verify(usersRepository).findByUsername(eq("username"));
    }

    @Test
    void itShouldThrowUsernameNotFoundException() {
        // Given
        when(usersRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        // When
        Throwable thrown = catchThrowable(() -> underTest.loadUserByUsername("username"));

        // Then
        verify(usersRepository).findByUsername(eq("username"));
        assertThat(thrown)
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found");
    }
}