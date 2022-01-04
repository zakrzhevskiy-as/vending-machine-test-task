package com.sbt.pprb.qa.test_task.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(schema = "vending_machine", name = "users")
public class AppUser extends AuditEntity {

    @Column(unique = true, nullable = false, updatable = false, length = 32)
    private String username;

    @JsonIgnore
    @ToString.Exclude
    @Column(nullable = false, updatable = false)
    private String password;
}
