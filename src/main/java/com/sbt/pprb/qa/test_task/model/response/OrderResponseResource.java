package com.sbt.pprb.qa.test_task.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponseResource {
    private Long id;
    private Integer orderNumber;
    private Integer balance;
    private Integer totalCost;
    private List<OrderBeverageResponseResource> orderBeverages;
    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss")
    private LocalDateTime created;
}
