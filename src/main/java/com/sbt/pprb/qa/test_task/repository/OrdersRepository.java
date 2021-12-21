package com.sbt.pprb.qa.test_task.repository;

import com.sbt.pprb.qa.test_task.model.dto.AppUser;
import com.sbt.pprb.qa.test_task.model.dto.Order;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdersRepository extends JpaRepository<Order, Long> {

    List<Order> findByOwnerOrderByIdDesc(AppUser owner);
    List<Order> findByOwnerAndActive(AppUser owner, Boolean active, Sort sort);
    void deleteAllByActive(Boolean active);
    void deleteAllByOwner(AppUser owner);
    void deleteAllByOwnerAndActive(AppUser owner, Boolean active);
}
