package com.sbt.pprb.qa.test_task.model;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.List;

@JsonView(Views.Beverage.class)
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(schema = "vending_machine", name = "BEVERAGES")
public class Beverage extends AuditEntity {

    @JsonView(Views.BeverageVolume.class)
    @Column(nullable = false)
    private Double availableVolume;

    @JsonView(Views.BeverageVolume.class)
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private BeverageType beverageType;

    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER, mappedBy = "beverage")
    private List<BeverageVolume> beverageVolumes;
}
