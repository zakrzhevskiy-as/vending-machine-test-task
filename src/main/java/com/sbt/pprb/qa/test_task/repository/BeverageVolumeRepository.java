package com.sbt.pprb.qa.test_task.repository;

import com.sbt.pprb.qa.test_task.model.Beverage;
import com.sbt.pprb.qa.test_task.model.BeverageVolume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BeverageVolumeRepository extends JpaRepository<BeverageVolume, Long> {
    List<BeverageVolume> getAllByBeverageId(Long beverageId);

    @Query(value =
            "update BeverageVolume bv " +
            "set bv.beverage = :beverage, bv.price = :price, bv.volume = :volume, bv.modified = current_timestamp " +
            "where bv.id = :id")
    BeverageVolume update(@Param("id") Long id,
                          @Param("beverage") Beverage beverage,
                          @Param("price") Integer price,
                          @Param("volume") Double volume);
}
