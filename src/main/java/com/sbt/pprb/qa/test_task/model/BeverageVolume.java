package com.sbt.pprb.qa.test_task.model;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(schema = "vending_machine", name = "beverage_volumes")
public class BeverageVolume extends AuditEntity {

    @JsonView(Views.BeverageVolume.class)
    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    @JoinColumn(name = "beverage_id")
    private Beverage beverage;

    @JsonView(Views.Beverage.class)
    @Column(nullable = false)
    private Double volume;

    @JsonView(Views.Beverage.class)
    @Column(nullable = false)
    private Integer price;
}
