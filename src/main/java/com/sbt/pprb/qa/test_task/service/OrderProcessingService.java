package com.sbt.pprb.qa.test_task.service;

import com.sbt.pprb.qa.test_task.model.dto.OrderBeverage;
import com.sbt.pprb.qa.test_task.model.dto.OrderBeverageStatus;
import com.sbt.pprb.qa.test_task.model.exception.InternalException;
import com.sbt.pprb.qa.test_task.repository.OrderBeveragesRepository;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class OrderProcessingService {

    private final OrderBeveragesRepository repository;
    private final JobLauncher jobLauncher;
    private final Job processBeverageJob;

    public void beveragesToStatus(OrderBeverageStatus status, OrderBeverage... beverages) {
        for (OrderBeverage beverage : beverages) {
            beverage.setStatus(status);
            repository.save(beverage);
        }
    }

    public void processBeverage(Long orderBeverageId, Long secondsToProcess) {
        JobParametersBuilder parametersBuilder = new JobParametersBuilder()
                .addLong("beverageId", orderBeverageId)
                .addLong("secondsToProcess", secondsToProcess);

        try {
            jobLauncher.run(processBeverageJob, parametersBuilder.toJobParameters());
        } catch (JobExecutionAlreadyRunningException | JobRestartException
                | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            throw new InternalException("Processing beverage job run failed", e);
        }
    }
}
