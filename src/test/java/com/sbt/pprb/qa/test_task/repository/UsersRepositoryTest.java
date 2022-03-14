package com.sbt.pprb.qa.test_task.repository;

import com.sbt.pprb.qa.test_task.model.dto.AppUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UsersRepositoryTest {

    @Autowired
    private UsersRepository underTest;

    @Test
    void itShouldFindExistingUserByUsername() {
        // given
        String username = "qa_engineer";

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