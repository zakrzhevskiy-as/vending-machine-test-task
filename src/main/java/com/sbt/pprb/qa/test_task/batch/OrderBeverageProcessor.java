package com.sbt.pprb.qa.test_task.batch;

import com.sbt.pprb.qa.test_task.model.dto.BeverageVolume;
import com.sbt.pprb.qa.test_task.model.dto.OrderBeverage;
import com.sbt.pprb.qa.test_task.model.dto.OrderBeverageStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.sbt.pprb.qa.test_task.model.dto.OrderBeverageStatus.PROCESSING;

@Slf4j
@Component
@StepScope
public class OrderBeverageProcessor implements ItemProcessor<OrderBeverage, OrderBeverage> {

    private long secondsToProcess;

    @Override
    public OrderBeverage process(OrderBeverage orderBeverage) throws Exception {
        if (orderBeverage.getStatus() == PROCESSING) {
            Thread.sleep(secondsToProcess * 1000);
            BeverageVolume beverageVolume = orderBeverage.getBeverageVolume();
            beverageVolume.getBeverage().subtractAvailableVolume(beverageVolume.getVolume());
        }
        OrderBeverageStatus nextStatus = orderBeverage.getStatus().getNextStatus();
        orderBeverage.setStatus(nextStatus);
        return orderBeverage;
    }

    @Value("#{jobParameters['secondsToProcess']}")
    public void setSecondsToProcess(final long secondsToProcess) {
        this.secondsToProcess = secondsToProcess;
    }
}
