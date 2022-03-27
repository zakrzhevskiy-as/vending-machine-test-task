package com.sbt.pprb.qa.test_task.model.response;

import com.sbt.pprb.qa.test_task.model.dto.BeverageType;
import com.sbt.pprb.qa.test_task.model.dto.OrderBeverageStatus;
import lombok.Data;

@Data
public class OrderBeverageResponseResource {
    private Long id;
    private BeverageType beverageType;
    private boolean selectedIce;
    private BeverageVolumeResponseResource beverageVolume;
    private OrderBeverageStatus status;
}
