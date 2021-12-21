package com.sbt.pprb.qa.test_task.model.response;

import com.sbt.pprb.qa.test_task.model.dto.BeverageType;
import lombok.Data;

@Data
public class OrderBeverageResponseResource {
    private Long id;
    private BeverageType beverageType;
    private Boolean selectedIce;
    private BeverageVolumeResponseResource beverageVolume;
}
