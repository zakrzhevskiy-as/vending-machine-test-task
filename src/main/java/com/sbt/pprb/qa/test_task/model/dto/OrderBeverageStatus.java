package com.sbt.pprb.qa.test_task.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;


@AllArgsConstructor
public enum OrderBeverageStatus {

    @JsonProperty("Selected")
    SELECTED(1, 2),
    @JsonProperty("Ready to process")
    READY_TO_PROCESS(2, 3),
    @JsonProperty("Processing")
    PROCESSING(3, 4),
    @JsonProperty("Ready")
    READY(4, 5),
    @JsonProperty("Taken")
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
