package com.sbt.pprb.qa.test_task.repository;

import com.sbt.pprb.qa.test_task.model.dto.Beverage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BeveragesRepository extends JpaRepository<Beverage, Long> {

    @Modifying(clearAutomatically = true)
    @Query(value = "update Beverage b set b.availableVolume = :availableVolume where b.id = :id")
    void update(@Param(value = "id") Long id, @Param(value = "availableVolume") Double availableVolume);
}
