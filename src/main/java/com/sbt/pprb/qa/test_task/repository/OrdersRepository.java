package com.sbt.pprb.qa.test_task.repository;

import com.sbt.pprb.qa.test_task.model.dto.AppUser;
import com.sbt.pprb.qa.test_task.model.dto.Order;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface OrdersRepository extends JpaRepository<Order, Long> {

    @Query(value = "SELECT nextval('vending_machine.orders_order_number_seq')", nativeQuery = true)
    Integer getNextOrderNumber();

    List<Order> findByOwnerAndActive(AppUser owner, Boolean active, Sort sort);

    void deleteAllByOwnerAndActive(AppUser owner, Boolean active);
}
