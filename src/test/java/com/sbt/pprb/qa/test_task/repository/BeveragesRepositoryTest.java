package com.sbt.pprb.qa.test_task.repository;

import com.sbt.pprb.qa.test_task.model.dto.Beverage;
import io.qameta.allure.Epic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Epic("Unit-тесты репозиториев")
@DisplayName("Тесты репозитория BeveragesRepository")
class BeveragesRepositoryTest {

    @Autowired
    private BeveragesRepository underTest;

    @Test
    void itShouldUpdateBeverageAvailableVolume() {
        // given
        Beverage base = underTest.findAll()
                .stream()
                .findAny()
                .orElseThrow(EntityNotFoundException::new);

        // when
        underTest.update(base.getId(), base.getAvailableVolume() + 10);

        // then
        Beverage result = underTest.getById(base.getId());
        assertThat(result.getAvailableVolume()).isNotEqualTo(base.getAvailableVolume());
    }
}