package com.sbt.pprb.qa.test_task.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(schema = "vending_machine", name = "beverage_volumes")
public class BeverageVolume extends AuditEntity {

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "beverage_id")
    private Beverage beverage;

    @Column(nullable = false)
    private Double volume;

    @Column(nullable = false)
    private Integer price;
}
