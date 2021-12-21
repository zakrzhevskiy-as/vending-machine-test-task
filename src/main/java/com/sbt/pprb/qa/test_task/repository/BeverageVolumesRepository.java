package com.sbt.pprb.qa.test_task.repository;

import com.sbt.pprb.qa.test_task.model.dto.BeverageVolume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BeverageVolumesRepository extends JpaRepository<BeverageVolume, Long> {
    List<BeverageVolume> getAllByBeverageId(Long beverageId);

    @Query(value =
            "update BeverageVolume bv " +
                    "set bv.price = :price, bv.volume = :volume, bv.modified = current_timestamp " +
                    "where bv.id = :id")
    BeverageVolume update(@Param("id") Long id,
                          @Param("price") Integer price,
                          @Param("volume") Double volume);
}
