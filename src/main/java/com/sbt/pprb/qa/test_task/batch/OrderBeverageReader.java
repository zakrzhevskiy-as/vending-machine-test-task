package com.sbt.pprb.qa.test_task.batch;

import com.sbt.pprb.qa.test_task.model.dto.OrderBeverage;
import com.sbt.pprb.qa.test_task.repository.OrderBeveragesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.sbt.pprb.qa.test_task.model.dto.OrderBeverageStatus.PROCESSING;
import static com.sbt.pprb.qa.test_task.model.dto.OrderBeverageStatus.READY_TO_PROCESS;

@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class OrderBeverageReader implements ItemReader<OrderBeverage> {

    private final OrderBeveragesRepository orderBeveragesRepository;
    private Long beverageId;

    @Override
    public OrderBeverage read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        Optional<OrderBeverage> beverage = orderBeveragesRepository.findByIdAndStatusIn(beverageId, READY_TO_PROCESS, PROCESSING);

        return beverage.orElse(null);
    }

    @Value("#{jobParameters['beverageId']}")
    public void setOrderId(final Long beverageId) {
        this.beverageId = beverageId;
    }
}
