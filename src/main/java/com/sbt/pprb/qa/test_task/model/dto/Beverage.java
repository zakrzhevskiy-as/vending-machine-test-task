package com.sbt.pprb.qa.test_task.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(schema = "vending_machine", name = "beverages")
public class Beverage extends AuditEntity {

    @Column(nullable = false)
    private Double availableVolume;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private BeverageType beverageType;

    public void subtractAvailableVolume(Double volume) {
        this.availableVolume -= volume;
    }

    public void addAvailableVolume(Double volume) {
        this.availableVolume += volume;
    }
}
