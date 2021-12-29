package com.sbt.pprb.qa.test_task.repository;

import com.sbt.pprb.qa.test_task.model.dto.OrderBeverage;
import com.sbt.pprb.qa.test_task.model.dto.OrderBeverageStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderBeveragesRepository extends JpaRepository<OrderBeverage, Long> {
    List<OrderBeverage> findByOrderId(Long orderId, Sort sort);
    Optional<OrderBeverage> findByIdAndStatusIn(Long beverageId, OrderBeverageStatus... statuses);
}
