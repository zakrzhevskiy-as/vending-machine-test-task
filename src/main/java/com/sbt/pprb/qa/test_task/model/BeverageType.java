package com.sbt.pprb.qa.test_task.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum  BeverageType {

    SLURM("Slurm"),
    NUKA_COLA("Nuka-Cola"),
    EXPRESSO("Экспрессо");

    private String type;
}
