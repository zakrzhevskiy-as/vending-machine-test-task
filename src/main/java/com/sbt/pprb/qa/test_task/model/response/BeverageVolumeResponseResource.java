package com.sbt.pprb.qa.test_task.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeverageVolumeResponseResource {
    private Long id;
    private Double volume;
    private Integer price;
}
