package com.sbt.pprb.qa.test_task.model.exception;

import com.sbt.pprb.qa.test_task.model.dto.OrderBeverage;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Необходимо забрать уже налитый напиток.")
public class BeverageCantBeProcessedException extends RuntimeException {

    public BeverageCantBeProcessedException(OrderBeverage notTakenBeverage) {
        super("There is not taken beverage: " + notTakenBeverage.getBeverageVolume().getBeverage().getBeverageType());
    }
}
