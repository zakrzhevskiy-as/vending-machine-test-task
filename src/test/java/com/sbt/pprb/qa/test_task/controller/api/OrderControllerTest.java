package com.sbt.pprb.qa.test_task.controller.api;

import com.sbt.pprb.qa.test_task.CommonTestContext;
import com.sbt.pprb.qa.test_task.VendingMachineApplication;
import com.sbt.pprb.qa.test_task.repository.OrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = VendingMachineApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class OrderControllerTest extends CommonTestContext {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private OrdersRepository repository;

    //@Test
    void getOrders_endpoint_test() {
    }

    //@Test
    void deleteOrders_endpoint_test() {
    }

    //@Test
    void getOrder_endpoint_test() {
    }

    //@Test
    void deleteOrder_endpoint_test() {
    }

    //@Test
    void createOrder_endpoint_test() {
    }

    //@Test
    void addBeverage_endpoint_test() {
    }

    //@Test
    void removeBeverage_endpoint_test() {
    }

    //@Test
    void processBeverage_endpoint_test() {
    }

    //@Test
    void addBalance_endpoint_test() {
    }

    //@Test
    void resetBalance_endpoint_test() {
    }

    //@Test
    void selectIce_endpoint_test() {
    }
}