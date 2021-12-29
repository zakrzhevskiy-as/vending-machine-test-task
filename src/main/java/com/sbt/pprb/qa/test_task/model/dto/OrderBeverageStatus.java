package com.sbt.pprb.qa.test_task.model.dto;

import lombok.AllArgsConstructor;


@AllArgsConstructor
public enum OrderBeverageStatus {

    SELECTED(1, 2),
    READY_TO_PROCESS(2, 3),
    PROCESSING(3, 4),
    READY(4, 5),
    TAKEN(5, null);

    private Integer order;
    private Integer nextStatus;

    public OrderBeverageStatus getNextStatus() {
        for (OrderBeverageStatus status : values()) {
            if (this.nextStatus == null) {
                return null;
            } else if (status.order.equals(this.nextStatus)) {
                return status;
            }
        }
        return null;
    }
}
