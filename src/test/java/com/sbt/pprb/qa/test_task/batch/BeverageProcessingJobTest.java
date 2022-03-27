package com.sbt.pprb.qa.test_task.batch;

import com.sbt.pprb.qa.test_task.config.TestConfig;
import com.sbt.pprb.qa.test_task.model.dto.*;
import com.sbt.pprb.qa.test_task.repository.BeveragesRepository;
import com.sbt.pprb.qa.test_task.repository.OrderBeveragesRepository;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@SpringBootTest
@Import(TestConfig.class)
class BeverageProcessingJobTest {

    private static final long secondsToProcess = 2;

    @MockBean
    private OrderBeveragesRepository orderBeveragesRepository;
    @MockBean
    private BeveragesRepository beveragesRepository;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    public void givenBeverage_whenJobExecuted_thenSuccess() throws Exception {
        // given
        Beverage beverage = new Beverage();
        beverage.setId(1L);
        beverage.setBeverageType(BeverageType.SLURM);
        beverage.setAvailableVolume(9.0);
        beverage.setCreated(LocalDateTime.now());
        beverage.setModified(LocalDateTime.now());

        BeverageVolume volume = new BeverageVolume();
        volume.setId(1L);
        volume.setBeverage(beverage);
        volume.setVolume(0.5);
        volume.setPrice(50);
        volume.setCreated(LocalDateTime.now());
        volume.setModified(LocalDateTime.now());

        OrderBeverage base = new OrderBeverage();
        base.setId(1L);
        base.setStatus(OrderBeverageStatus.READY_TO_PROCESS);
        base.setBeverageVolume(volume);
        base.setSelectedIce(false);

        OrderBeverage edited = new OrderBeverage();
        edited.setId(base.getId());
        edited.setStatus(OrderBeverageStatus.READY);
        base.setBeverageVolume(volume);
        base.setSelectedIce(false);

        when(orderBeveragesRepository.findById(any())).thenReturn(Optional.of(base));
        when(orderBeveragesRepository.saveAllAndFlush(anyList())).thenReturn(Collections.singletonList(edited));
        when(beveragesRepository.save(any())).thenReturn(beverage);

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(defaultJobParameters());
        JobInstance actualJobInstance = jobExecution.getJobInstance();
        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();
        Date startTime = jobExecution.getStartTime();
        Date endTime = jobExecution.getEndTime();
        long secondsProcessed = (endTime.getTime() - startTime.getTime()) / 1000;
        Collection<StepExecution> stepExecutions = jobExecution.getStepExecutions();

        // then
        assertThat(actualJobInstance.getJobName()).isEqualTo("processBeverageJob");
        assertThat(actualJobExitStatus.getExitCode()).isEqualTo("COMPLETED");
        assertThat(endTime).isAfter(startTime);
        assertThat(secondsProcessed).isCloseTo(secondsToProcess, Offset.offset(1L));
        assertThat(stepExecutions)
                .map(stepExecution -> stepExecution.getExitStatus().getExitCode())
                .isNotEmpty()
                .hasSize(2)
                .doesNotContain("UNKNOWN", "EXECUTING", "NOOP", "FAILED", "STOPPED");
    }

    private JobParameters defaultJobParameters() {
        return new JobParametersBuilder()
                .addLong("beverageId", 1L)
                .addLong("secondsToProcess", secondsToProcess)
                .toJobParameters();
    }
}
