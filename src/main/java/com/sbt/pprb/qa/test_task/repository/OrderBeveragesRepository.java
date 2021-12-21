package com.sbt.pprb.qa.test_task.repository;

import com.sbt.pprb.qa.test_task.model.dto.OrderBeverage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderBeveragesRepository extends JpaRepository<OrderBeverage, Long> {
    List<OrderBeverage> findByOrderId(Long orderId);
}
