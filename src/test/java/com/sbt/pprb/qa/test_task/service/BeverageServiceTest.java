package com.sbt.pprb.qa.test_task.service;

import com.sbt.pprb.qa.test_task.model.dto.Beverage;
import com.sbt.pprb.qa.test_task.model.dto.BeverageType;
import com.sbt.pprb.qa.test_task.model.dto.BeverageVolume;
import com.sbt.pprb.qa.test_task.repository.BeverageVolumesRepository;
import com.sbt.pprb.qa.test_task.repository.BeveragesRepository;
import io.qameta.allure.Epic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Epic("Unit-тесты сервисов")
@DisplayName("Тесты сервиса BeverageService")
class BeverageServiceTest {

    @Mock
    private BeveragesRepository beveragesRepository;
    @Mock
    private BeverageVolumesRepository beverageVolumesRepository;
    private BeverageService underTest;

    @BeforeEach
    void setUp() {
        underTest = new BeverageService(beveragesRepository, beverageVolumesRepository);
    }

    @Test
    void canGetBeverages() {
        // given
        when(beveragesRepository.findAll(any(Sort.class))).thenReturn(emptyList());

        // when
        underTest.getBeverages();

        // then
        verify(beveragesRepository).findAll(any(Sort.class));
    }

    @Test
    void canGetVolumes() {
        // given
        List<BeverageVolume> beverageVolumes = new ArrayList<>();
        Beverage beverage = new Beverage();
        beverage.setId((long) 1);
        beverage.setBeverageType(BeverageType.SLURM);
        beverage.setAvailableVolume(5.5);

        for (int i = 1; i <= 3; i++) {
            BeverageVolume beverageVolume = new BeverageVolume();
            beverageVolume.setId((long) i);
            beverageVolume.setBeverage(beverage);
            beverageVolume.setVolume(0.33);
            beverageVolume.setPrice(50);
            beverageVolumes.add(beverageVolume);
        }

        when(beverageVolumesRepository.findAll(any(Sort.class))).thenReturn(beverageVolumes);

        // when
        underTest.getVolumes();

        // then
        verify(beverageVolumesRepository).findAll(any(Sort.class));
    }

    @Test
    void canAddVolume() {
        // given
        Beverage base = new Beverage();
        base.setId((long) 1);
        base.setBeverageType(BeverageType.SLURM);
        base.setAvailableVolume(7.5);

        when(beveragesRepository.getById(base.getId())).thenReturn(base);

        // when
        underTest.addVolume(base.getId(), 1.2);

        // then
        verify(beveragesRepository).update(base.getId(), base.getAvailableVolume() + 1.2);
    }
}