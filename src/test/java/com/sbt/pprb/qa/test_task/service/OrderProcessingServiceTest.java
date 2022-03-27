package com.sbt.pprb.qa.test_task.service;

import com.sbt.pprb.qa.test_task.model.dto.*;
import com.sbt.pprb.qa.test_task.model.exception.InternalException;
import com.sbt.pprb.qa.test_task.repository.OrderBeveragesRepository;
import io.qameta.allure.Epic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;

import java.time.LocalDateTime;
import java.util.Date;

import static java.time.ZoneId.systemDefault;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

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
    void canProcessBeverage() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        // given
        JobExecution jobExecution = new JobExecution(1L);
        jobExecution.setStatus(BatchStatus.COMPLETED);
        LocalDateTime time = LocalDateTime.now();
        Date startTime = Date.from(time.minus(5L, SECONDS).atZone(systemDefault()).toInstant());
        Date endTime = Date.from(time.plus(5L, SECONDS).atZone(systemDefault()).toInstant());
        jobExecution.setCreateTime(startTime);
        jobExecution.setStartTime(startTime);
        jobExecution.setEndTime(endTime);

        when(jobLauncher.run(any(Job.class), any(JobParameters.class))).thenReturn(jobExecution);

        // when
        underTest.processBeverage(1L, 1L);

        // then
        verify(jobLauncher).run(any(Job.class), any(JobParameters.class));
    }

    @Test
    void throwsExceptionWhenRunJob() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        // given
        doThrow(new JobParametersInvalidException("The JobParameters can not be null"))
                .when(jobLauncher)
                .run(any(Job.class), any(JobParameters.class));

        // when
        Throwable thrown = catchThrowable(() -> underTest.processBeverage(1L, 1L));

        // then
        assertThat(thrown)
                .isInstanceOf(InternalException.class)
                .hasMessage("Processing beverage job run failed");
    }
}