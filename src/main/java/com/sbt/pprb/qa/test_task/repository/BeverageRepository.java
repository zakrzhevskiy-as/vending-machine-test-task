package com.sbt.pprb.qa.test_task.repository;

import com.sbt.pprb.qa.test_task.model.Beverage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BeverageRepository extends JpaRepository<Beverage, Long> {

    @Query(value = "update Beverage b set b.availableVolume = :availableVolume where b.id = :id")
    Beverage update(@Param(value = "id") Long id, @Param(value = "availableVolume") Double availableVolume);
}
