package com.sbt.pprb.qa.test_task.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BeverageType {

    @JsonProperty("Slurm")
    SLURM("Slurm"),
    @JsonProperty("Nuka-Cola")
    NUKA_COLA("Nuka-Cola"),
    @JsonProperty("Экспрессо")
    EXPRESSO("Экспрессо");

    private String type;
}
