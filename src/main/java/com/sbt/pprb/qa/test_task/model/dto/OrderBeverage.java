package com.sbt.pprb.qa.test_task.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(schema = "vending_machine", name = "orders_beverages")
@SequenceGenerator(name = "default_gen", schema = "vending_machine", sequenceName = "orders_beverages_id_seq", allocationSize = 1)
public class OrderBeverage extends AuditEntity {

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    private Boolean selectedIce;

    @ManyToOne(cascade = CascadeType.DETACH)
    private BeverageVolume beverageVolume;

    @Enumerated(EnumType.STRING)
    private OrderBeverageStatus status;
}
