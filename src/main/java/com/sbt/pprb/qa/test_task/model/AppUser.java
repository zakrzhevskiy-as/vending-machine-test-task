package com.sbt.pprb.qa.test_task.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(schema = "vending_machine", name = "users")
public class AppUser extends AuditEntity {

    @Column(unique = true, nullable = false, updatable = false, length = 32)
    private String username;

    @Column(nullable = false, updatable = false)
    private String password;

    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER, mappedBy = "owner")
    private List<Order> orders;
}
