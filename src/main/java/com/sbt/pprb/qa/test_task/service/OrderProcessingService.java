package com.sbt.pprb.qa.test_task.service;

import com.sbt.pprb.qa.test_task.model.dto.OrderBeverage;
import com.sbt.pprb.qa.test_task.model.dto.OrderBeverageStatus;
import com.sbt.pprb.qa.test_task.repository.OrderBeveragesRepository;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

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

    @SneakyThrows
    public void processBeverage(Long orderBeverageId) {
        JobParameter beverageIdParam = new JobParameter(orderBeverageId);
        Map<String, JobParameter> parameterMap = new HashMap<>();
        parameterMap.put("beverageId", beverageIdParam);
        JobParameters parameters = new JobParameters(parameterMap);

        jobLauncher.run(processBeverageJob, parameters);
    }
}
