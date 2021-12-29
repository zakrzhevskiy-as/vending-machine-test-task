package com.sbt.pprb.qa.test_task.batch;

import com.sbt.pprb.qa.test_task.model.dto.Beverage;
import com.sbt.pprb.qa.test_task.model.dto.OrderBeverage;
import com.sbt.pprb.qa.test_task.repository.BeveragesRepository;
import com.sbt.pprb.qa.test_task.repository.OrderBeveragesRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@StepScope
@AllArgsConstructor
public class OrderBeverageWriter implements ItemWriter<OrderBeverage> {

    private BeveragesRepository beveragesRepository;
    private OrderBeveragesRepository orderBeveragesRepository;

    @Override
    public void write(List<? extends OrderBeverage> list) throws Exception {
        list.forEach(orderBeverage -> {
            Beverage beverage = orderBeverage.getBeverageVolume().getBeverage();
            beveragesRepository.save(beverage);
        });

        orderBeveragesRepository.saveAllAndFlush(list);
    }
}
