package com.sbt.pprb.qa.test_task.repository;

import com.sbt.pprb.qa.test_task.model.dto.Beverage;
import com.sbt.pprb.qa.test_task.model.dto.BeverageVolume;
import com.sbt.pprb.qa.test_task.model.exception.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BeverageVolumesRepositoryTest {

    @Autowired
    private BeverageVolumesRepository underTest;
    @Autowired
    private BeveragesRepository beveragesRepository;

    @Test
    void itShouldUpdatePriceAndModificationDate() {
        // given
        BeverageVolume base = underTest.findAll().stream().findAny().orElseThrow(EntityNotFoundException::new);

        // when
        underTest.update(base.getId(), base.getPrice() + 10, base.getVolume());

        // then
        BeverageVolume result = underTest.getById(base.getId());
        assertThat(result.getPrice()).isNotEqualTo(base.getPrice());
        assertThat(result.getModified()).isNotEqualTo(base.getModified());
    }

    @Test
    void itShouldUpdateVolumeAndModificationDate() {
        // given
        BeverageVolume base = underTest.findAll().stream().findAny().orElseThrow(EntityNotFoundException::new);

        // when
        underTest.update(base.getId(), base.getPrice(), base.getVolume() + 1);

        // then
        BeverageVolume result = underTest.getById(base.getId());
        assertThat(result.getVolume()).isNotEqualTo(base.getVolume());
        assertThat(result.getModified()).isNotEqualTo(base.getModified());
    }

    @Test
    void itShouldUpdatePriceAndVolumeAndModificationDate() {
        // given
        BeverageVolume base = underTest.findAll().stream().findAny().orElseThrow(EntityNotFoundException::new);

        // when
        underTest.update(base.getId(), base.getPrice() + 10, base.getVolume() + 1);

        // then
        BeverageVolume result = underTest.getById(base.getId());
        assertThat(result.getPrice()).isNotEqualTo(base.getPrice());
        assertThat(result.getVolume()).isNotEqualTo(base.getVolume());
        assertThat(result.getModified()).isNotEqualTo(base.getModified());
    }

    @Test
    void itShouldFindBeverageVolumesByBeverage() {
        // given
        Beverage beverage = beveragesRepository.findAll()
                .stream()
                .findAny()
                .orElseThrow(EntityNotFoundException::new);

        // when
        List<BeverageVolume> result = underTest.findBeverageVolumeByBeverage(beverage);

        // then
        assertThat(result).isNotEmpty();
    }
}