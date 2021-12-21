package com.sbt.pprb.qa.test_task.model.response;

import com.sbt.pprb.qa.test_task.model.dto.BeverageType;
import lombok.Data;

import java.util.List;

@Data
public class BeverageResponseResource {
    private Long id;
    private Double availableVolume;
    private BeverageType beverageType;
    private List<BeverageVolumeResponseResource> beverageVolumes;
}
