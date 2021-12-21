package com.sbt.pprb.qa.test_task.model.response;

import lombok.Data;

import java.util.List;

@Data
public class OrderResponseResource {
    private Long id;
    private Integer balance;
    private Integer totalCost;
    private List<OrderBeverageResponseResource> orderBeverages;
}
