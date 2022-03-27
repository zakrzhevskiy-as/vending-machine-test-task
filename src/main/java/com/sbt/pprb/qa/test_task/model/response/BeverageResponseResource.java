package com.sbt.pprb.qa.test_task.model.response;

import com.sbt.pprb.qa.test_task.model.dto.BeverageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeverageResponseResource implements Comparable<BeverageResponseResource> {

    private Long id;
    private Double availableVolume;
    private BeverageType beverageType;
    private List<BeverageVolumeResponseResource> beverageVolumes;

    @Override
    public int compareTo(BeverageResponseResource o) {
        return id.compareTo(o.getId());
    }
}
