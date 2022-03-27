package com.sbt.pprb.qa.test_task.config;

import com.sbt.pprb.qa.test_task.model.dto.OrderBeverage;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@AllArgsConstructor
@EnableBatchProcessing
public class BatchJobConfig {

    public JobBuilderFactory jobs;
    public StepBuilderFactory steps;
    private PlatformTransactionManager transactionManager;

    @Bean
    public Job processBeverageJob(@Qualifier("step1") Step step1, @Qualifier("step2") Step step2) {
        return jobs.get("processBeverageJob")
                .start(step1)
                .next(step2)
                .build();
    }

    @Bean
    public Step step1(StepBuilderFactory steps,
                      ItemReader<OrderBeverage> reader,
                      ItemProcessor<OrderBeverage, OrderBeverage> processor,
                      ItemWriter<OrderBeverage> writer) {

        return steps.get("startProcessing")
                .<OrderBeverage, OrderBeverage> chunk(1)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .transactionManager(transactionManager)
                .build();
    }

    @Bean
    public Step step2(ItemReader<OrderBeverage> reader,
                     ItemProcessor<OrderBeverage, OrderBeverage> processor,
                     ItemWriter<OrderBeverage> writer) {

        return steps.get("processingFinished")
                .<OrderBeverage, OrderBeverage> chunk(1)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .transactionManager(transactionManager)
                .build();
    }
}