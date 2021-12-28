package com.sbt.pprb.qa.test_task.service;

import com.sbt.pprb.qa.test_task.model.dto.*;
import com.sbt.pprb.qa.test_task.model.exception.BeverageCantBeSelectedException;
import com.sbt.pprb.qa.test_task.model.exception.EntityNotFoundException;
import com.sbt.pprb.qa.test_task.model.response.BeverageVolumeResponseResource;
import com.sbt.pprb.qa.test_task.model.response.OrderBeverageResponseResource;
import com.sbt.pprb.qa.test_task.model.response.OrderResponseResource;
import com.sbt.pprb.qa.test_task.repository.*;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class OrderService {

    private OrdersRepository ordersRepository;
    private UsersRepository usersRepository;
    private OrderBeveragesRepository orderBeveragesRepository;
    private BeveragesRepository beveragesRepository;
    private BeverageVolumesRepository volumeRepository;

    public List<OrderResponseResource> getOrders(AppUser owner, Boolean active) {
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        List<Order> byOwnerAndActive = ordersRepository.findByOwnerAndActive(owner, active, sort);

        return byOwnerAndActive.stream().map(this::getOrderResponseResource).collect(Collectors.toList());
    }

    public void deleteActive(AppUser owner) {
        ordersRepository.deleteAllByOwnerAndActive(owner, true);
    }

    public void deleteFinished(AppUser owner) {
        ordersRepository.deleteAllByOwnerAndActive(owner, false);
        resetBeveragesAvailableVolume();
    }

    public void resetBeveragesAvailableVolume() {
        Beverage slurm = beveragesRepository.findByBeverageType(BeverageType.SLURM);
        slurm.setAvailableVolume(5.0);
        beveragesRepository.save(slurm);

        Beverage nukaCola = beveragesRepository.findByBeverageType(BeverageType.NUKA_COLA);
        nukaCola.setAvailableVolume(8.4);
        beveragesRepository.save(nukaCola);

        Beverage expresso = beveragesRepository.findByBeverageType(BeverageType.EXPRESSO);
        expresso.setAvailableVolume(3.0);
        beveragesRepository.save(expresso);
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

    private OrderResponseResource getOrderResponseResource(Order order) {
        OrderResponseResource responseResource = new OrderResponseResource();
        responseResource.setId(order.getId());
        responseResource.setOrderNumber(order.getOrderNumber());
        responseResource.setCreated(order.getCreated());

        Sort sort = Sort.by(Sort.Direction.ASC, "created");
        List<OrderBeverage> orderBeverages = orderBeveragesRepository.findByOrderId(order.getId(), sort);
        List<OrderBeverageResponseResource> beverageResponseResources = orderBeverages.stream()
                .map(this::getOrderBeverageResponseResource)
                .collect(Collectors.toList());

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
        beverageResponseResource.setSelectedIce(orderBeverage.getSelectedIce());
        beverageResponseResource.setStatus(orderBeverage.getStatus());

        BeverageVolumeResponseResource volumeResponseResource = new BeverageVolumeResponseResource();
        volumeResponseResource.setId(orderBeverage.getBeverageVolume().getId());
        volumeResponseResource.setVolume(orderBeverage.getBeverageVolume().getVolume());
        volumeResponseResource.setPrice(orderBeverage.getBeverageVolume().getPrice());

        beverageResponseResource.setBeverageVolume(volumeResponseResource);
        return beverageResponseResource;
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
            throw new BeverageCantBeSelectedException("Lack of " + beverage.getBeverageType().getType());
        }

        orderBeverage.setOrder(order);
        orderBeverage.setBeverageVolume(beverageVolume);
        orderBeverage.setSelectedIce(false);
        orderBeverage.setStatus(OrderBeverageStatus.SELECTED);

        OrderBeverage saved = orderBeveragesRepository.save(orderBeverage);

        return getOrderBeverageResponseResource(saved);
    }

    public void submitOrder(Long id) {
        Order order = ordersRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Order", id));
        order.setActive(false);

        Sort sort = Sort.by(Sort.Direction.ASC, "created");
        orderBeveragesRepository.findByOrderId(id, sort).forEach(orderBeverage -> {
            BeverageVolume beverageVolume = orderBeverage.getBeverageVolume();
            Beverage beverage = beverageVolume.getBeverage();

            beverage.subtractAvailableVolume(beverageVolume.getVolume());
            beveragesRepository.save(beverage);
        });

        ordersRepository.save(order);

        AppUser user = usersRepository.getById(order.getOwner().getId());
        usersRepository.save(user);
    }

    public void removeBeverage(Long beverageId) {
        orderBeveragesRepository.deleteById(beverageId);
    }

    public OrderResponseResource addBalance(Long orderId, Integer amount) {
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
}
