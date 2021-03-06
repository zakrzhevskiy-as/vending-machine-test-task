package com.sbt.pprb.qa.test_task.service;

import com.sbt.pprb.qa.test_task.model.dto.*;
import com.sbt.pprb.qa.test_task.model.exception.BeverageCantBeProcessedException;
import com.sbt.pprb.qa.test_task.model.exception.BeverageCantBeSelectedException;
import com.sbt.pprb.qa.test_task.model.exception.EntityNotFoundException;
import com.sbt.pprb.qa.test_task.model.exception.FakeCoinException;
import com.sbt.pprb.qa.test_task.model.response.BeverageVolumeResponseResource;
import com.sbt.pprb.qa.test_task.model.response.OrderBeverageResponseResource;
import com.sbt.pprb.qa.test_task.model.response.OrderResponseResource;
import com.sbt.pprb.qa.test_task.repository.BeverageVolumesRepository;
import com.sbt.pprb.qa.test_task.repository.OrderBeveragesRepository;
import com.sbt.pprb.qa.test_task.repository.OrdersRepository;
import com.sbt.pprb.qa.test_task.repository.UsersRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
public class OrderService {

    private final OrdersRepository ordersRepository;
    private final UsersRepository usersRepository;
    private final OrderBeveragesRepository orderBeveragesRepository;
    private final BeverageVolumesRepository volumeRepository;

    private final OrderProcessingService processingService;

    public List<OrderResponseResource> getOrders(AppUser owner, Boolean active) {
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        List<Order> byOwnerAndActive = ordersRepository.findByOwnerAndActive(owner, active, sort);

        return byOwnerAndActive.stream().map(this::getOrderResponseResource).collect(toList());
    }

    public void deleteFinished(AppUser owner) {
        // ОЖИДАЕМАЯ ОШИБКА - не удаляются завершенные заказы
        ordersRepository.deleteAllByOwnerAndActive(owner, false);
    }

    public void deleteOrder(Long orderId) {
        List<OrderBeverage> beverages = orderBeveragesRepository.findByOrderId(orderId, Sort.unsorted());
        orderBeveragesRepository.deleteAll(beverages);
        ordersRepository.deleteById(orderId);
    }

    public OrderResponseResource getOrder(Long id) {
        Order order = ordersRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Order", id));

        return getOrderResponseResource(order);
    }

    public Order createOrder(AppUser user) {
        Order newOrder = new Order();
        newOrder.setOrderNumber(ordersRepository.getNextOrderNumber());
        newOrder.setOwner(user);
        newOrder.setActive(true);
        newOrder.setBalance(0);

        Order created = ordersRepository.save(newOrder);
        usersRepository.save(user);

        return created;
    }

    public OrderBeverageResponseResource addBeverage(Long orderId, OrderBeverage orderBeverage) {
        Order order = ordersRepository
                .findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order", orderId));

        Map<BeverageType, Double> orderBeveragesVolume = new HashMap<>();

        Sort sort = Sort.by(Sort.Direction.ASC, "created");
        for (OrderBeverage ob : orderBeveragesRepository.findByOrderId(orderId, sort)) {
            BeverageVolume beverageVolume = ob.getBeverageVolume();
            Beverage beverage = beverageVolume.getBeverage();
            BeverageType beverageType = beverage.getBeverageType();
            Double volume = beverageVolume.getVolume();

            if (orderBeveragesVolume.containsKey(beverageType)) {
                Double previousVolume = orderBeveragesVolume.get(beverageType);
                orderBeveragesVolume.put(beverageType, previousVolume + volume);
            } else {
                orderBeveragesVolume.put(beverageType, volume);
            }
        }

        Long beverageVolumeId = orderBeverage.getBeverageVolume().getId();
        BeverageVolume beverageVolume = volumeRepository
                .findById(beverageVolumeId)
                .orElseThrow(() -> new EntityNotFoundException("Beverage volume", beverageVolumeId));
        Beverage beverage = beverageVolume.getBeverage();
        BeverageType savingBeverageType = beverage.getBeverageType();

        Double orderBeverageCurrentVolume = orderBeveragesVolume.get(savingBeverageType) != null
                ? orderBeveragesVolume.get(savingBeverageType)
                : 0D;

        if (beverage.getAvailableVolume() - (orderBeverageCurrentVolume + beverageVolume.getVolume()) < 0) {
            throw new BeverageCantBeSelectedException();
        }

        orderBeverage.setOrder(order);
        orderBeverage.setBeverageVolume(beverageVolume);
        orderBeverage.setSelectedIce(false);
        orderBeverage.setStatus(OrderBeverageStatus.SELECTED);

        OrderBeverage saved = orderBeveragesRepository.save(orderBeverage);

        return getOrderBeverageResponseResource(saved);
    }

    public List<OrderBeverageResponseResource> submitOrder(Long id) {
        Sort sort = Sort.by(Sort.Direction.ASC, "created");
        List<OrderBeverage> beverages = orderBeveragesRepository.findByOrderId(id, sort);
        processingService.beveragesToStatus(OrderBeverageStatus.READY_TO_PROCESS, beverages.toArray(new OrderBeverage[]{}));

        return orderBeveragesRepository.findByOrderId(id, sort)
                .stream()
                .map(this::getOrderBeverageResponseResource)
                .collect(toList());
    }

    public List<OrderBeverageResponseResource> processBeverage(Long orderId,
                                                               Long beverageId,
                                                               ProcessAction action,
                                                               Long secondsToProcess) {
        Sort sort = Sort.by(Sort.Direction.ASC, "created");
        List<OrderBeverage> beverages = orderBeveragesRepository.findByOrderId(orderId, sort);

        switch (action) {
            case PROCESS:
                beverages.stream()
                        .filter(orderBeverage -> orderBeverage.getStatus() == OrderBeverageStatus.READY)
                        .findAny()
                        .ifPresent(orderBeverage -> {
                            throw new BeverageCantBeProcessedException(orderBeverage);
                        });

                processingService.processBeverage(beverageId, secondsToProcess);
                break;
            case TAKE:
                OrderBeverage beverage = orderBeveragesRepository.getById(beverageId);
                OrderBeverageStatus nextStatus = beverage.getStatus().getNextStatus();
                processingService.beveragesToStatus(nextStatus, beverage);
                long takenCount = beverages.stream()
                        .filter(ob -> ob.getStatus() == OrderBeverageStatus.TAKEN)
                        .count();
                long readyCount = beverages.stream()
                        .filter(ob -> ob.getStatus() == OrderBeverageStatus.READY)
                        .count();

                if (beverages.size() - (readyCount + takenCount) == 0) {
                    Order order = ordersRepository.getById(orderId);
                    order.setActive(false);
                    ordersRepository.save(order);

                    return Collections.emptyList();
                }
                break;
            default:
                throw new IllegalArgumentException("Action type SUBMIT not supported");
        }

        return orderBeveragesRepository.findByOrderId(orderId, sort)
                .stream()
                .map(this::getOrderBeverageResponseResource)
                .collect(toList());
    }

    public void removeBeverage(Long beverageId) {
        orderBeveragesRepository.deleteById(beverageId);
    }

    public OrderResponseResource addBalance(Long orderId, Integer amount) {
        // ОЖИДАЕМАЯ ОШИБКА - при пополнении на 10₽ значение заменяется на 500₽
        if (amount == 10) {
            amount = 500;
        }

        // ОЖИДАЕМАЯ ОШИБКА - при пополнении на 2₽ возвращается ошибка
        if (amount == 2) {
            throw new FakeCoinException();
        }

        Order order = ordersRepository.findById(orderId).orElseThrow(() -> new EntityNotFoundException("Order", orderId));
        Integer currentBalance = order.getBalance();
        order.setBalance(currentBalance + amount);
        Order updated = ordersRepository.save(order);

        return getOrderResponseResource(updated);
    }

    public OrderResponseResource resetBalance(Long orderId) {
        Order order = ordersRepository.findById(orderId).orElseThrow(() -> new EntityNotFoundException("Order", orderId));
        order.setBalance(0);
        return getOrderResponseResource(ordersRepository.save(order));
    }

    public OrderBeverageResponseResource selectIce(Long beverageId, Boolean value) {
        OrderBeverage orderBeverage = orderBeveragesRepository.getById(beverageId);
        orderBeverage.setSelectedIce(value);
        return getOrderBeverageResponseResource(orderBeveragesRepository.save(orderBeverage));
    }

    public OrderResponseResource getOrderResponseResource(Order order) {
        OrderResponseResource responseResource = new OrderResponseResource();
        responseResource.setId(order.getId());
        responseResource.setOrderNumber(order.getOrderNumber());
        responseResource.setCreated(order.getCreated());

        Sort sort = Sort.by(Sort.Direction.ASC, "created");
        List<OrderBeverage> orderBeverages = orderBeveragesRepository.findByOrderId(order.getId(), sort);
        List<OrderBeverageResponseResource> beverageResponseResources = orderBeverages.stream()
                .map(this::getOrderBeverageResponseResource)
                .collect(toList());

        responseResource.setOrderBeverages(beverageResponseResources);

        Integer totalCost = beverageResponseResources.stream()
                .mapToInt(beverage -> beverage.getBeverageVolume().getPrice())
                .sum();
        responseResource.setTotalCost(totalCost);
        responseResource.setBalance(order.getBalance());

        return responseResource;
    }

    private OrderBeverageResponseResource getOrderBeverageResponseResource(OrderBeverage orderBeverage) {
        OrderBeverageResponseResource beverageResponseResource = new OrderBeverageResponseResource();
        beverageResponseResource.setId(orderBeverage.getId());
        beverageResponseResource.setBeverageType(orderBeverage.getBeverageVolume().getBeverage().getBeverageType());
        beverageResponseResource.setSelectedIce(orderBeverage.isSelectedIce());
        beverageResponseResource.setStatus(orderBeverage.getStatus());

        BeverageVolumeResponseResource volumeResponseResource = new BeverageVolumeResponseResource();
        volumeResponseResource.setId(orderBeverage.getBeverageVolume().getId());
        volumeResponseResource.setVolume(orderBeverage.getBeverageVolume().getVolume());
        volumeResponseResource.setPrice(orderBeverage.getBeverageVolume().getPrice());

        beverageResponseResource.setBeverageVolume(volumeResponseResource);
        return beverageResponseResource;
    }
}
