package com.sbt.pprb.qa.test_task.service;

import com.sbt.pprb.qa.test_task.model.Order;
import com.sbt.pprb.qa.test_task.repository.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class OrderService {

    private OrderRepository repository;

    public List<Order> getOrders() {
        return repository.findAll();
    }

    public Order getOrder(Long id) {
        return repository.getById(id);
    }

    public Order createOrder(Order order) {
        return repository.save(order);
    }

}
