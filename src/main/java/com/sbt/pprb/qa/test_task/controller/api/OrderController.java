package com.sbt.pprb.qa.test_task.controller.api;

import com.sbt.pprb.qa.test_task.model.dto.AppUser;
import com.sbt.pprb.qa.test_task.model.dto.Order;
import com.sbt.pprb.qa.test_task.model.dto.OrderBeverage;
import com.sbt.pprb.qa.test_task.model.response.OrderBeverageResponseResource;
import com.sbt.pprb.qa.test_task.model.response.OrderResponseResource;
import com.sbt.pprb.qa.test_task.service.OrderService;
import com.sbt.pprb.qa.test_task.service.UserService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/orders")
public class OrderController {

    private UserService userService;
    private OrderService orderService;

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Order>> getOrders(Principal principal) {
        Optional<AppUser> user = userService.getUser(principal.getName());
        if (user.isPresent()) {
            return ResponseEntity.ok(orderService.getOrders(user.get()));
        } else {
            return ResponseEntity.status(SC_UNAUTHORIZED).build();
        }
    }

    @DeleteMapping
    public void deleteOrders(@RequestParam(required = false, defaultValue = "true") Boolean deleteAll,
                             Principal principal, HttpServletResponse response) throws IOException {

        Optional<AppUser> user = userService.getUser(principal.getName());
        if (user.isPresent()) {
            AppUser owner = user.get();
            if (deleteAll) {
                orderService.deleteActive(owner);
            }
            orderService.deleteFinished(owner);

            response.setStatus(HttpStatus.NO_CONTENT.value());
        } else {
            response.sendError(SC_UNAUTHORIZED);
        }
    }

    @GetMapping(path = "{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderResponseResource> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrder(id));
    }

    @SneakyThrows
    @PostMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createOrder(Principal principal) {
        Optional<AppUser> user = userService.getUser(principal.getName());
        if (user.isPresent()) {
            Order created = orderService.createOrder(user.get());
            return ResponseEntity.created(new URI("/api/orders/" + created.getId())).body(created);
        } else {
            return ResponseEntity.status(SC_UNAUTHORIZED).build();
        }
    }

    @DeleteMapping(path = "{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT, reason = "Successfully deleted")
    public void deleteOrder(@PathVariable Long id, HttpServletResponse response) {
        orderService.deleteOrder(id);
        response.setStatus(204);
    }

    @GetMapping(path = "active")
    public ResponseEntity<OrderResponseResource> getActiveOrder(Principal principal) {
        Optional<AppUser> user = userService.getUser(principal.getName());
        if (user.isPresent()) {
            List<OrderResponseResource> activeOrder = orderService.getActiveOrder(user.get());
            if (!activeOrder.isEmpty()) {
                return ResponseEntity.ok(activeOrder.get(0));
            } else {
                return ResponseEntity.status(NOT_FOUND).build();
            }
        } else {
            return ResponseEntity.status(SC_UNAUTHORIZED).build();
        }
    }

    @GetMapping(path = "finished")
    public ResponseEntity<List<OrderResponseResource>> getFinishedOrders(Principal principal) {
        Optional<AppUser> user = userService.getUser(principal.getName());
        if (user.isPresent()) {
            return ResponseEntity.ok(orderService.getDoneOrders(user.get()));
        } else {
            return ResponseEntity.status(SC_UNAUTHORIZED).build();
        }
    }

    @SneakyThrows
    @PutMapping(path = "{id}/beverages", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderBeverageResponseResource> addBeverage(@PathVariable Long id, @RequestBody OrderBeverage beverage) {
        return ResponseEntity.ok(orderService.addBeverage(id, beverage));
    }

    @PostMapping(path = "{id}/submit")
    public void submitOrder(@PathVariable Long id) {
        orderService.submitOrder(id);
    }

    @DeleteMapping(path = "beverages/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT, reason = "Successfully deleted.")
    public void removeBeverage(@PathVariable Long id, HttpServletResponse response) {
        orderService.removeBeverage(id);
        response.setStatus(204);
    }

    @GetMapping(value = "{id}/beverages", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<OrderBeverage>> getOrderBeverages(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderBeverages(id));
    }

    @PutMapping(value = "{id}/add-balance")
    public ResponseEntity<OrderResponseResource> addBalance(@PathVariable Long id, @RequestParam Integer amount) {
        return ResponseEntity.ok(orderService.addBalance(id, amount));
    }

    @PatchMapping(value = "{id}/reset-balance")
    public ResponseEntity<OrderResponseResource> resetBalance(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.resetBalance(id));
    }

    @PatchMapping(value = "beverages/{id}/select-ice")
    public ResponseEntity<OrderBeverageResponseResource> selectIce(@PathVariable(name = "id") Long beverageId,
                                                                   @RequestParam Boolean withIce) {

        return ResponseEntity.ok(orderService.selectIce(beverageId, withIce));
    }
}
