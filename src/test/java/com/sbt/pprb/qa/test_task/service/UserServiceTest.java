package com.sbt.pprb.qa.test_task.service;

import com.sbt.pprb.qa.test_task.model.dto.AppUser;
import com.sbt.pprb.qa.test_task.model.exception.UsernameAlreadyTakenException;
import com.sbt.pprb.qa.test_task.repository.UsersRepository;
import io.qameta.allure.Epic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@Epic("Unit-тесты сервисов")
@DisplayName("Тесты сервиса UserService")
class UserServiceTest {

    @Mock
    private UsersRepository usersRepository;
    private UserService underTest;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

    @BeforeEach
    void setUp() {
        underTest = new UserService(usersRepository);
    }

    @Test
    void canGetUserByUsername() {
        // when
        underTest.getUser("test_user");
        // then
        verify(usersRepository).findByUsername("test_user");
    }

    @Test
    void canCreateUser() {
        // given
        AppUser user = new AppUser();
        user.setUsername("test_user");
        user.setPassword(passwordEncoder.encode("password"));
        user.setEnabled(true);
        user.setAuthority("USER");

        // when
        underTest.createUser(user);

        // then
        ArgumentCaptor<AppUser> userArgumentCaptor = ArgumentCaptor.forClass(AppUser.class);
        verify(usersRepository).save(userArgumentCaptor.capture());
        AppUser capturedUser = userArgumentCaptor.getValue();

        assertThat(capturedUser).isEqualTo(user);
    }

    @Test
    void willThrowWhenUsernameIsTaken() {
        // given
        AppUser user = new AppUser();
        user.setUsername("test_user");
        user.setPassword(passwordEncoder.encode("password"));
        user.setEnabled(true);
        user.setAuthority("USER");

        given(usersRepository.findByUsername(anyString()))
                .willReturn(Optional.of(user));

        // when -> then
        assertThatThrownBy(() -> underTest.createUser(user))
                .isInstanceOf(UsernameAlreadyTakenException.class)
                .hasMessageContaining("Username '" + user.getUsername() + "' already taken");
        verify(usersRepository, never()).save(any());
    }
}