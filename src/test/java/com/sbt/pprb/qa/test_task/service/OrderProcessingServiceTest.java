package com.sbt.pprb.qa.test_task.service;

import com.sbt.pprb.qa.test_task.model.dto.*;
import com.sbt.pprb.qa.test_task.repository.OrderBeveragesRepository;
import io.qameta.allure.Epic;
import io.qameta.allure.Muted;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@Epic("Unit-тесты сервисов")
@DisplayName("Тесты сервиса OrderProcessingService")
class OrderProcessingServiceTest {

    @Mock
    private OrderBeveragesRepository orderBeveragesRepository;
    @Mock
    private JobLauncher jobLauncher;
    @Mock
    private Job job;
    private OrderProcessingService underTest;

    @BeforeEach
    void setUp() {
        underTest = new OrderProcessingService(orderBeveragesRepository, jobLauncher, job);
    }

    @Test
    void canPassBeveragesToStatus() {
        // given
        AppUser user = new AppUser();
        user.setUsername("test_user");
        user.setPassword("password");
        user.setEnabled(true);
        user.setAuthority("USER");

        Order order = new Order();
        order.setOrderNumber(1000);
        order.setActive(true);
        order.setOwner(user);
        order.setBalance(100);

        Beverage beverage = new Beverage();
        beverage.setBeverageType(BeverageType.SLURM);
        beverage.setAvailableVolume(5.6);

        BeverageVolume beverageVolume = new BeverageVolume();
        beverageVolume.setBeverage(beverage);
        beverageVolume.setVolume(0.33);
        beverageVolume.setPrice(50);

        OrderBeverage orderBeverage = new OrderBeverage();
        orderBeverage.setOrder(order);
        orderBeverage.setBeverageVolume(beverageVolume);
        orderBeverage.setStatus(OrderBeverageStatus.SELECTED);
        orderBeverage.setSelectedIce(false);

        // when
        underTest.beveragesToStatus(OrderBeverageStatus.READY_TO_PROCESS, orderBeverage);

        // then
        verify(orderBeveragesRepository).save(orderBeverage);
    }

    @Test
    @Disabled("can't implement job test")
    @Muted
    void canProcessBeverage() throws Exception {
        // given
//        AppUser user = new AppUser();
//        user.setUsername("test_user");
//        user.setPassword("password");
//        user.setEnabled(true);
//        user.setAuthority("USER");
//
//        Order order = new Order();
//        order.setOrderNumber(1000);
//        order.setActive(true);
//        order.setOwner(user);
//        order.setBalance(100);
//
//        Beverage beverage = new Beverage();
//        beverage.setBeverageType(BeverageType.SLURM);
//        beverage.setAvailableVolume(5.6);
//
//        BeverageVolume beverageVolume = new BeverageVolume();
//        beverageVolume.setBeverage(beverage);
//        beverageVolume.setVolume(0.33);
//        beverageVolume.setPrice(50);
//
//        OrderBeverage orderBeverage = new OrderBeverage();
//        orderBeverage.setOrder(order);
//        orderBeverage.setBeverageVolume(beverageVolume);
//        orderBeverage.setStatus(OrderBeverageStatus.READY_TO_PROCESS);
//        orderBeverage.setSelectedIce(false);
//
//        when(orderBeveragesRepository.findById(anyLong())).thenReturn(Optional.of(orderBeverage));

        // when
//        JobExecution jobExecution = jobLauncherTestUtils.launchJob(defaultJobParameters(orderBeverage.getId()));
//        JobInstance jobInstance = jobExecution.getJobInstance();
//        ExitStatus exitStatus = jobExecution.getExitStatus()

        // then
    }

    @Test
    @Disabled("can't implement job test")
    @Muted
    void throwsExceptionWhenRunJob() {
        // given
        // when
        // then
    }
}