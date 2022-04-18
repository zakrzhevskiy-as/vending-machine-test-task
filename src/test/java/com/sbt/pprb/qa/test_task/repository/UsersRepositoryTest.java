package com.sbt.pprb.qa.test_task.repository;

import com.sbt.pprb.qa.test_task.model.dto.AppUser;
import io.qameta.allure.Epic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Epic("Unit-тесты репозиториев")
@DisplayName("Тесты репозитория UsersRepository")
class UsersRepositoryTest {

    @Autowired
    private UsersRepository underTest;

    @Value("${system.rest.auth.username}")
    private String username;

    @Test
    void itShouldFindExistingUserByUsername() {
        // when
        Optional<AppUser> result = underTest.findByUsername(username);

        // then
        assertThat(result).isNotEmpty();
    }

    @Test
    void itShouldNotFindUserByUsername() {
        // given
        String username = "test_user";

        // when
        Optional<AppUser> result = underTest.findByUsername(username);

        // then
        assertThat(result).isEmpty();
    }
}