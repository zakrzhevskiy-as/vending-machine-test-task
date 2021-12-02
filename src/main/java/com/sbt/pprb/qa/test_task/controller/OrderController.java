package com.sbt.pprb.qa.test_task.controller;

import com.sbt.pprb.qa.test_task.model.Order;
import com.sbt.pprb.qa.test_task.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/orders")
public class OrderController {

    private OrderService service;

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Order>> getOrders() {
        return ResponseEntity.ok(service.getOrders());
    }

    @GetMapping(path = "{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(service.getOrder(id));
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Order> createOrder(Order order) throws URISyntaxException {
        Order created = service.createOrder(order);
        return ResponseEntity.created(new URI("/api/orders/" + created.getId())).body(created);
    }
}
