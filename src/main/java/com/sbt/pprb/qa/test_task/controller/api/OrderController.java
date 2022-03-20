package com.sbt.pprb.qa.test_task.controller.api;

import com.sbt.pprb.qa.test_task.model.dto.AppUser;
import com.sbt.pprb.qa.test_task.model.dto.Order;
import com.sbt.pprb.qa.test_task.model.dto.OrderBeverage;
import com.sbt.pprb.qa.test_task.model.dto.ProcessAction;
import com.sbt.pprb.qa.test_task.model.response.OrderBeverageResponseResource;
import com.sbt.pprb.qa.test_task.model.response.OrderResponseResource;
import com.sbt.pprb.qa.test_task.service.OrderService;
import com.sbt.pprb.qa.test_task.service.UserService;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.sbt.pprb.qa.test_task.controller.EndpointPaths.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@AllArgsConstructor
@RequestMapping(path = ORDERS)
public class OrderController {

    private final UserService userService;
    private final OrderService orderService;

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses({
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not Found")
    })
    public ResponseEntity<List<OrderResponseResource>> getOrders(@ApiIgnore Principal principal,
                                                                 @RequestParam(required = false, defaultValue = "false") Boolean active) {

        Optional<AppUser> user = userService.getUser(principal.getName());
        List<OrderResponseResource> orders = orderService.getOrders(user.get(), active);
        if (active && orders.isEmpty()) {
            return ResponseEntity.status(NOT_FOUND).build();
        }
        return ResponseEntity.ok(active ? Collections.singletonList(orders.get(0)) : orders);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses({
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not Found")
    })
    public void deleteFinishedOrders(@ApiIgnore Principal principal,
                                     @ApiIgnore HttpServletResponse response) throws IOException {

        Optional<AppUser> user = userService.getUser(principal.getName());

        AppUser owner = user.get();
        orderService.deleteFinished(owner);

        response.setStatus(HttpStatus.NO_CONTENT.value());
    }

    @GetMapping(path = ORDERS_ID, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses({
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not Found")
    })
    public ResponseEntity<OrderResponseResource> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrder(id));
    }

    @DeleteMapping(path = ORDERS_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses({
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not Found")
    })
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }

    @SneakyThrows
    @PostMapping(produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponse(code = 401, message = "Unauthorized")
    public ResponseEntity<OrderResponseResource> createOrder(@ApiIgnore Principal principal) {
        Optional<AppUser> user = userService.getUser(principal.getName());
        Order created = orderService.createOrder(user.get());
        OrderResponseResource responseResource = orderService.getOrderResponseResource(created);
        URI uri = new URI(String.join(File.separator, ORDERS, String.valueOf(created.getId())));
        return ResponseEntity.created(uri).body(responseResource);
    }

    @SneakyThrows
    @PutMapping(path = ORDERS_ID_BEVERAGES, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses({
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not Found")
    })
    public ResponseEntity<OrderBeverageResponseResource> addBeverage(@PathVariable Long id,
                                                                     @RequestBody OrderBeverage beverage) {
        return ResponseEntity.ok(orderService.addBeverage(id, beverage));
    }

    @DeleteMapping(path = ORDERS_BEVERAGES_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses({
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not Found")
    })
    public void removeBeverage(@PathVariable Long id, @ApiIgnore HttpServletResponse response) {
        orderService.removeBeverage(id);
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }

    @PostMapping(path = ORDERS_ID)
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses({
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not Found")
    })
    public ResponseEntity<List<OrderBeverageResponseResource>> process(@PathVariable(name = "id") Long orderId,
                                                                       @RequestParam(required = false) Long beverageId,
                                                                       @RequestParam ProcessAction action) {

        if (action == ProcessAction.SUBMIT) {
            return ResponseEntity.ok(orderService.submitOrder(orderId));
        } else {
            return ResponseEntity.ok(orderService.processBeverage(orderId, beverageId, action));
        }
    }

    @PutMapping(value = ORDERS_ID_BALANCE)
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses({
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not Found")
    })
    public ResponseEntity<OrderResponseResource> addBalance(@PathVariable Long id, @RequestParam Integer amount) {
        return ResponseEntity.ok(orderService.addBalance(id, amount));
    }

    @DeleteMapping(value = ORDERS_ID_BALANCE)
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses({
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not Found")
    })
    public ResponseEntity<OrderResponseResource> resetBalance(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.resetBalance(id));
    }

    @PatchMapping(value = ORDERS_BEVERAGES_ID_ICE)
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses({
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not Found")
    })
    public ResponseEntity<OrderBeverageResponseResource> selectIce(@PathVariable(name = "id") Long beverageId,
                                                                   @RequestParam Boolean withIce) {

        return ResponseEntity.ok(orderService.selectIce(beverageId, withIce));
    }
}
