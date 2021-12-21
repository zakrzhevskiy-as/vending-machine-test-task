package com.sbt.pprb.qa.test_task.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "orders_beverages")
public class OrderBeverage extends AuditEntity {

    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    private Boolean selectedIce;

    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    private BeverageVolume beverageVolume;
}
