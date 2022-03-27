package com.sbt.pprb.qa.test_task.service;

import com.sbt.pprb.qa.test_task.model.dto.*;
import com.sbt.pprb.qa.test_task.model.exception.BeverageCantBeProcessedException;
import com.sbt.pprb.qa.test_task.model.exception.BeverageCantBeSelectedException;
import com.sbt.pprb.qa.test_task.model.exception.EntityNotFoundException;
import com.sbt.pprb.qa.test_task.model.exception.FakeCoinException;
import com.sbt.pprb.qa.test_task.model.response.OrderBeverageResponseResource;
import com.sbt.pprb.qa.test_task.model.response.OrderResponseResource;
import com.sbt.pprb.qa.test_task.repository.BeverageVolumesRepository;
import com.sbt.pprb.qa.test_task.repository.OrderBeveragesRepository;
import com.sbt.pprb.qa.test_task.repository.OrdersRepository;
import com.sbt.pprb.qa.test_task.repository.UsersRepository;
import io.qameta.allure.Epic;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Epic("Unit-тесты сервисов")
@DisplayName("Тесты сервиса OrderService")
class OrderServiceTest {

    @Mock
    private OrdersRepository ordersRepository;
    @Mock
    private UsersRepository usersRepository;
    @Mock
    private OrderBeveragesRepository orderBeveragesRepository;
    @Mock
    private BeverageVolumesRepository volumeRepository;
    @Mock
    private OrderProcessingService processingService;
    private OrderService underTest;

    @BeforeEach
    void setUp() {
        underTest = new OrderService(ordersRepository,
                usersRepository,
                orderBeveragesRepository,
                volumeRepository,
                processingService);
    }

    @Test
    void canGetActiveOrder() {
        // given
        AppUser user = new AppUser();
        user.setUsername("test_user");
        user.setPassword("password");
        user.setEnabled(true);
        user.setAuthority("USER");

        Order order = new Order();
        order.setOrderNumber(1001);
        order.setActive(true);
        order.setBalance(100);
        order.setOwner(user);

        when(ordersRepository.findByOwnerAndActive(any(AppUser.class), eq(true), any(Sort.class)))
                .thenReturn(singletonList(order));

        // when
        underTest.getOrders(user, true);

        // then
        verify(ordersRepository).findByOwnerAndActive(eq(user), eq(true), any(Sort.class));
    }

    @Test
    void canGetFinishedOrders() {
        // given
        AppUser user = new AppUser();
        user.setUsername("test_user");
        user.setPassword("password");
        user.setEnabled(true);
        user.setAuthority("USER");

        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Order order = new Order();
            order.setOrderNumber(1001);
            order.setActive(false);
            order.setBalance(100);
            order.setOwner(user);
            orders.add(order);
        }
        when(ordersRepository.findByOwnerAndActive(any(AppUser.class), eq(false), any(Sort.class)))
                .thenReturn(orders);

        // when
        underTest.getOrders(user, false);

        // then
        verify(ordersRepository).findByOwnerAndActive(eq(user), eq(false), any(Sort.class));
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    void cantDeleteFinishedOrders() {
        // given
        AppUser user = new AppUser();
        user.setUsername("test_user");
        user.setPassword("password");
        user.setEnabled(true);
        user.setAuthority("USER");

        doThrow(new DataIntegrityViolationException("could not execute statement"))
                .when(ordersRepository).deleteAllByOwnerAndActive(user, false);

        // when
        Throwable thrown = catchThrowable(() -> underTest.deleteFinished(user));

        // then
        assertThat(thrown)
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessage("could not execute statement");
    }

    @Test
    void canDeleteOrder() {
        // given
        AppUser user = new AppUser();
        user.setUsername("test_user");
        user.setPassword("password");
        user.setEnabled(true);
        user.setAuthority("USER");

        Order order = new Order();
        order.setOrderNumber(1000);
        order.setActive(true);
        order.setOwner(user);
        order.setBalance(100);

        Beverage beverage = new Beverage();
        beverage.setBeverageType(BeverageType.SLURM);
        beverage.setAvailableVolume(5.6);

        BeverageVolume beverageVolume = new BeverageVolume();
        beverageVolume.setBeverage(beverage);
        beverageVolume.setVolume(0.33);
        beverageVolume.setPrice(50);

        OrderBeverage orderBeverage = new OrderBeverage();
        orderBeverage.setOrder(order);
        orderBeverage.setBeverageVolume(beverageVolume);
        orderBeverage.setStatus(OrderBeverageStatus.SELECTED);
        orderBeverage.setSelectedIce(false);

        when(orderBeveragesRepository.findByOrderId(eq(order.getId()), any(Sort.class)))
                .thenReturn(singletonList(orderBeverage));

        // when
        underTest.deleteOrder(order.getId());

        // then
        verify(orderBeveragesRepository).findByOrderId(eq(order.getId()), any(Sort.class));
    }

    @Test
    void canGetOrder() {
        // given
        AppUser user = new AppUser();
        user.setUsername("test_user");
        user.setPassword("password");
        user.setEnabled(true);
        user.setAuthority("USER");

        Order order = new Order();
        order.setId(1L);
        order.setOrderNumber(1001);
        order.setBalance(100);
        order.setActive(true);
        order.setOwner(user);
        order.setCreated(LocalDateTime.now());
        order.setModified(LocalDateTime.now());

        when(ordersRepository.findById(anyLong())).thenReturn(Optional.of(order));

        // when
        underTest.getOrder(order.getId());

        // then
        verify(ordersRepository).findById(eq(order.getId()));
    }

    @Test
    void getOrderThrowsEntityNotFoundException() {
        // given
        when(ordersRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when
        Throwable thrown = catchThrowable(() -> underTest.getOrder(1L));

        // then
        assertThat(thrown)
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Order with id='1' not exist.");
    }

    @Test
    void canCreateOrder() {
        // given
        Integer orderNumber = 1001;

        AppUser user = new AppUser();
        user.setUsername("test_user");
        user.setPassword("password");
        user.setEnabled(true);
        user.setAuthority("USER");

        Order newOrder = new Order();
        newOrder.setOrderNumber(orderNumber);
        newOrder.setOwner(user);
        newOrder.setActive(true);
        newOrder.setBalance(0);

        Order created = new Order();
        created.setId(1L);
        created.setOrderNumber(newOrder.getOrderNumber());
        created.setBalance(newOrder.getBalance());
        created.setActive(newOrder.getActive());
        created.setOwner(newOrder.getOwner());
        created.setCreated(LocalDateTime.now());
        created.setModified(LocalDateTime.now());

        when(ordersRepository.getNextOrderNumber()).thenReturn(orderNumber);
        when(ordersRepository.save(any(Order.class))).thenReturn(created);

        // when
        underTest.createOrder(user);

        // then
        verify(ordersRepository).getNextOrderNumber();
        verify(ordersRepository).save(any(Order.class));
        verify(usersRepository).save(user);
    }

    @Test
    void canAddNewBeverage() {
        // given
        AppUser user = new AppUser();
        user.setUsername("test_user");
        user.setPassword("password");
        user.setEnabled(true);
        user.setAuthority("USER");

        Order order = new Order();
        order.setId(1L);
        order.setOrderNumber(1001);
        order.setBalance(100);
        order.setActive(true);
        order.setOwner(user);
        order.setCreated(LocalDateTime.now());
        order.setModified(LocalDateTime.now());

        BeverageVolume beverageVolume = new BeverageVolume();
        beverageVolume.setId(1L);

        OrderBeverage selected = new OrderBeverage();
        selected.setBeverageVolume(beverageVolume);

        Beverage beverage = new Beverage();
        beverage.setId(1L);
        beverage.setBeverageType(BeverageType.SLURM);
        beverage.setAvailableVolume(5.0);
        beverage.setCreated(LocalDateTime.now());
        beverage.setModified(LocalDateTime.now());

        BeverageVolume fullBeverageVolume = new BeverageVolume();
        fullBeverageVolume.setId(1L);
        fullBeverageVolume.setBeverage(beverage);
        fullBeverageVolume.setVolume(0.5);
        fullBeverageVolume.setPrice(50);
        fullBeverageVolume.setCreated(LocalDateTime.now());
        fullBeverageVolume.setModified(LocalDateTime.now());

        List<OrderBeverage> orderBeverages = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Beverage b = new Beverage();
            b.setId((long) i);
            b.setBeverageType(i % 2 == 0 ? BeverageType.SLURM : BeverageType.NUKA_COLA);
            b.setAvailableVolume(5.0);
            b.setCreated(LocalDateTime.now());
            b.setModified(LocalDateTime.now());

            BeverageVolume bv = new BeverageVolume();
            bv.setId((long) i);
            bv.setBeverage(b);
            bv.setVolume(0.5);
            bv.setPrice(50);
            bv.setCreated(LocalDateTime.now());
            bv.setModified(LocalDateTime.now());

            OrderBeverage ob = new OrderBeverage();
            ob.setId((long) i);
            ob.setOrder(order);
            ob.setBeverageVolume(bv);
            ob.setStatus(OrderBeverageStatus.SELECTED);
            ob.setSelectedIce(false);
            ob.setCreated(LocalDateTime.now());
            ob.setModified(LocalDateTime.now());

            orderBeverages.add(ob);
        }

        OrderBeverage saved = new OrderBeverage();
        saved.setId(1L);
        saved.setOrder(order);
        saved.setBeverageVolume(fullBeverageVolume);
        saved.setStatus(OrderBeverageStatus.SELECTED);
        saved.setSelectedIce(false);
        saved.setCreated(LocalDateTime.now());
        saved.setModified(LocalDateTime.now());

        when(ordersRepository.findById(eq(order.getId()))).thenReturn(Optional.of(order));
        when(orderBeveragesRepository.findByOrderId(eq(order.getId()), any(Sort.class))).thenReturn(orderBeverages);
        when(volumeRepository.findById(anyLong())).thenReturn(Optional.of(fullBeverageVolume));
        when(orderBeveragesRepository.save(any(OrderBeverage.class))).thenReturn(saved);

        // when
        underTest.addBeverage(order.getId(), selected);

        // then
        verify(orderBeveragesRepository).save(any(OrderBeverage.class));
    }

    @Test
    void canAddMoreBeverage() {
        // given
        AppUser user = new AppUser();
        user.setUsername("test_user");
        user.setPassword("password");
        user.setEnabled(true);
        user.setAuthority("USER");

        Order order = new Order();
        order.setId(1L);
        order.setOrderNumber(1001);
        order.setBalance(100);
        order.setActive(true);
        order.setOwner(user);
        order.setCreated(LocalDateTime.now());
        order.setModified(LocalDateTime.now());

        BeverageVolume beverageVolume = new BeverageVolume();
        beverageVolume.setId(1L);

        OrderBeverage selected = new OrderBeverage();
        selected.setBeverageVolume(beverageVolume);

        Beverage beverage = new Beverage();
        beverage.setId(1L);
        beverage.setBeverageType(BeverageType.EXPRESSO);
        beverage.setAvailableVolume(5.0);
        beverage.setCreated(LocalDateTime.now());
        beverage.setModified(LocalDateTime.now());

        BeverageVolume fullBeverageVolume = new BeverageVolume();
        fullBeverageVolume.setId(1L);
        fullBeverageVolume.setBeverage(beverage);
        fullBeverageVolume.setVolume(0.5);
        fullBeverageVolume.setPrice(50);
        fullBeverageVolume.setCreated(LocalDateTime.now());
        fullBeverageVolume.setModified(LocalDateTime.now());

        OrderBeverage saved = new OrderBeverage();
        saved.setId(1L);
        saved.setOrder(order);
        saved.setBeverageVolume(fullBeverageVolume);
        saved.setStatus(OrderBeverageStatus.SELECTED);
        saved.setSelectedIce(false);
        saved.setCreated(LocalDateTime.now());
        saved.setModified(LocalDateTime.now());

        when(ordersRepository.findById(eq(order.getId()))).thenReturn(Optional.of(order));
        when(orderBeveragesRepository.findByOrderId(eq(order.getId()), any(Sort.class))).thenReturn(emptyList());
        when(volumeRepository.findById(anyLong())).thenReturn(Optional.of(fullBeverageVolume));
        when(orderBeveragesRepository.save(any(OrderBeverage.class))).thenReturn(saved);

        // when
        underTest.addBeverage(order.getId(), selected);

        // then
        verify(orderBeveragesRepository).save(any(OrderBeverage.class));
    }

    @Test
    void addBeverageThrowsEntityNotFoundException_beverageVolume() {
        // given
        AppUser user = new AppUser();
        user.setUsername("test_user");
        user.setPassword("password");
        user.setEnabled(true);
        user.setAuthority("USER");

        Order order = new Order();
        order.setId(1L);
        order.setOrderNumber(1001);
        order.setBalance(100);
        order.setActive(true);
        order.setOwner(user);
        order.setCreated(LocalDateTime.now());
        order.setModified(LocalDateTime.now());

        BeverageVolume beverageVolume = new BeverageVolume();
        beverageVolume.setId(1L);

        OrderBeverage selected = new OrderBeverage();
        selected.setBeverageVolume(beverageVolume);

        when(ordersRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(orderBeveragesRepository.findByOrderId(anyLong(), any(Sort.class))).thenReturn(emptyList());
        when(volumeRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when
        Throwable thrown = catchThrowable(() -> underTest.addBeverage(order.getId(), selected));

        // then
        assertThat(thrown)
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Beverage volume with id='1' not exist.");
        verify(orderBeveragesRepository, never()).save(any());
    }

    @Test
    void addBeverageThrowsBeverageCantBeSelectedException() {
        // given
        AppUser user = new AppUser();
        user.setUsername("test_user");
        user.setPassword("password");
        user.setEnabled(true);
        user.setAuthority("USER");

        Order order = new Order();
        order.setId(1L);
        order.setOrderNumber(1001);
        order.setBalance(100);
        order.setActive(true);
        order.setOwner(user);
        order.setCreated(LocalDateTime.now());
        order.setModified(LocalDateTime.now());

        BeverageVolume beverageVolume = new BeverageVolume();
        beverageVolume.setId(1L);

        OrderBeverage selected = new OrderBeverage();
        selected.setBeverageVolume(beverageVolume);

        Beverage beverage = new Beverage();
        beverage.setId(1L);
        beverage.setBeverageType(BeverageType.EXPRESSO);
        beverage.setAvailableVolume(0.1);
        beverage.setCreated(LocalDateTime.now());
        beverage.setModified(LocalDateTime.now());

        BeverageVolume fullBeverageVolume = new BeverageVolume();
        fullBeverageVolume.setId(1L);
        fullBeverageVolume.setBeverage(beverage);
        fullBeverageVolume.setVolume(0.5);
        fullBeverageVolume.setPrice(50);
        fullBeverageVolume.setCreated(LocalDateTime.now());
        fullBeverageVolume.setModified(LocalDateTime.now());

        OrderBeverage saved = new OrderBeverage();
        saved.setId(1L);
        saved.setOrder(order);
        saved.setBeverageVolume(fullBeverageVolume);
        saved.setStatus(OrderBeverageStatus.SELECTED);
        saved.setSelectedIce(false);
        saved.setCreated(LocalDateTime.now());
        saved.setModified(LocalDateTime.now());

        when(ordersRepository.findById(eq(order.getId()))).thenReturn(Optional.of(order));
        when(orderBeveragesRepository.findByOrderId(eq(order.getId()), any(Sort.class))).thenReturn(emptyList());
        when(volumeRepository.findById(anyLong())).thenReturn(Optional.of(fullBeverageVolume));

        // when
        Throwable thrown = catchThrowable(() -> underTest.addBeverage(order.getId(), selected));

        // then
        assertThat(thrown).isInstanceOf(BeverageCantBeSelectedException.class);
        verify(orderBeveragesRepository, never()).save(any());
    }

    @Test
    void addBeverageThrowsEntityNotFoundException_order() {
        // given
        when(ordersRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when
        Throwable thrown = catchThrowable(() -> underTest.addBeverage(1L, null));

        // then
        assertThat(thrown)
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Order with id='1' not exist.");
        verify(orderBeveragesRepository, never()).findByOrderId(any(), any());
        verify(volumeRepository, never()).findById(any());
        verify(orderBeveragesRepository, never()).save(any());
    }

    @Test
    void canSubmitOrder() {
        // given
        AppUser user = new AppUser();
        user.setUsername("test_user");
        user.setPassword("password");
        user.setEnabled(true);
        user.setAuthority("USER");

        Order order = new Order();
        order.setId(1L);
        order.setOrderNumber(1001);
        order.setBalance(100);
        order.setActive(true);
        order.setOwner(user);
        order.setCreated(LocalDateTime.now());
        order.setModified(LocalDateTime.now());

        List<OrderBeverage> orderBeverages = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Beverage beverage = new Beverage();
            beverage.setId(1L);
            beverage.setAvailableVolume(5.0);
            beverage.setBeverageType(BeverageType.SLURM);

            BeverageVolume beverageVolume = new BeverageVolume();
            beverageVolume.setBeverage(beverage);
            beverageVolume.setPrice(50);
            beverageVolume.setVolume(0.33);

            OrderBeverage orderBeverage = new OrderBeverage();
            orderBeverage.setOrder(order);
            orderBeverage.setStatus(OrderBeverageStatus.SELECTED);
            orderBeverage.setBeverageVolume(beverageVolume);
            orderBeverages.add(orderBeverage);
        }

        when(orderBeveragesRepository.findByOrderId(anyLong(), any(Sort.class))).thenReturn(orderBeverages);

        // when
        underTest.submitOrder(order.getId());

        // then
        verify(orderBeveragesRepository, times(2))
                .findByOrderId(eq(order.getId()), any(Sort.class));
    }

    @Test
    void canProcessBeverage() {
        // given
        AppUser user = new AppUser();
        user.setUsername("test_user");
        user.setPassword("password");
        user.setEnabled(true);
        user.setAuthority("USER");

        Order order = new Order();
        order.setId(1L);
        order.setOrderNumber(1001);
        order.setBalance(100);
        order.setActive(true);
        order.setOwner(user);
        order.setCreated(LocalDateTime.now());
        order.setModified(LocalDateTime.now());

        Beverage beverage = new Beverage();
        beverage.setId(1L);
        beverage.setBeverageType(BeverageType.EXPRESSO);
        beverage.setAvailableVolume(0.1);
        beverage.setCreated(LocalDateTime.now());
        beverage.setModified(LocalDateTime.now());

        BeverageVolume fullBeverageVolume = new BeverageVolume();
        fullBeverageVolume.setId(1L);
        fullBeverageVolume.setBeverage(beverage);
        fullBeverageVolume.setVolume(0.5);
        fullBeverageVolume.setPrice(50);
        fullBeverageVolume.setCreated(LocalDateTime.now());
        fullBeverageVolume.setModified(LocalDateTime.now());

        OrderBeverage saved = new OrderBeverage();
        saved.setId(1L);
        saved.setOrder(order);
        saved.setBeverageVolume(fullBeverageVolume);
        saved.setStatus(OrderBeverageStatus.SELECTED);
        saved.setSelectedIce(false);
        saved.setCreated(LocalDateTime.now());
        saved.setModified(LocalDateTime.now());

        // when
        underTest.processBeverage(order.getId(), saved.getId(), ProcessAction.PROCESS);

        // then
        verify(processingService).processBeverage(eq(order.getId()));
    }

    @Test
    void canTakeBeverage() {
        // given
        AppUser user = new AppUser();
        user.setUsername("test_user");
        user.setPassword("password");
        user.setEnabled(true);
        user.setAuthority("USER");

        Order order = new Order();
        order.setId(1L);
        order.setOrderNumber(1001);
        order.setBalance(100);
        order.setActive(true);
        order.setOwner(user);
        order.setCreated(LocalDateTime.now());
        order.setModified(LocalDateTime.now());

        Beverage beverage = new Beverage();
        beverage.setId(1L);
        beverage.setBeverageType(BeverageType.EXPRESSO);
        beverage.setAvailableVolume(0.1);
        beverage.setCreated(LocalDateTime.now());
        beverage.setModified(LocalDateTime.now());

        BeverageVolume fullBeverageVolume = new BeverageVolume();
        fullBeverageVolume.setId(1L);
        fullBeverageVolume.setBeverage(beverage);
        fullBeverageVolume.setVolume(0.5);
        fullBeverageVolume.setPrice(50);
        fullBeverageVolume.setCreated(LocalDateTime.now());
        fullBeverageVolume.setModified(LocalDateTime.now());

        OrderBeverage orderBeverage1 = new OrderBeverage();
        orderBeverage1.setId(1L);
        orderBeverage1.setOrder(order);
        orderBeverage1.setBeverageVolume(fullBeverageVolume);
        orderBeverage1.setStatus(OrderBeverageStatus.TAKEN);
        orderBeverage1.setSelectedIce(false);
        orderBeverage1.setCreated(LocalDateTime.now());
        orderBeverage1.setModified(LocalDateTime.now());

        OrderBeverage orderBeverage2 = new OrderBeverage();
        orderBeverage2.setId(2L);
        orderBeverage2.setOrder(order);
        orderBeverage2.setBeverageVolume(fullBeverageVolume);
        orderBeverage2.setStatus(OrderBeverageStatus.READY);
        orderBeverage2.setSelectedIce(false);
        orderBeverage2.setCreated(LocalDateTime.now());
        orderBeverage2.setModified(LocalDateTime.now());

        when(orderBeveragesRepository.findByOrderId(anyLong(), any(Sort.class)))
                .thenReturn(asList(orderBeverage1, orderBeverage2));
        when(orderBeveragesRepository.getById(orderBeverage2.getId())).thenReturn(orderBeverage2);
        when(ordersRepository.getById(anyLong())).thenReturn(order);

        // when
        underTest.processBeverage(order.getId(), orderBeverage2.getId(), ProcessAction.TAKE);

        // then
        verify(processingService).beveragesToStatus(orderBeverage2.getStatus().getNextStatus(), orderBeverage2);
    }

    @Test
    void canTakeLastBeverage() {
        // given
        AppUser user = new AppUser();
        user.setUsername("test_user");
        user.setPassword("password");
        user.setEnabled(true);
        user.setAuthority("USER");

        Order order = new Order();
        order.setId(1L);
        order.setOrderNumber(1001);
        order.setBalance(100);
        order.setActive(true);
        order.setOwner(user);
        order.setCreated(LocalDateTime.now());
        order.setModified(LocalDateTime.now());

        Beverage beverage = new Beverage();
        beverage.setId(1L);
        beverage.setBeverageType(BeverageType.EXPRESSO);
        beverage.setAvailableVolume(0.1);
        beverage.setCreated(LocalDateTime.now());
        beverage.setModified(LocalDateTime.now());

        BeverageVolume fullBeverageVolume = new BeverageVolume();
        fullBeverageVolume.setId(1L);
        fullBeverageVolume.setBeverage(beverage);
        fullBeverageVolume.setVolume(0.5);
        fullBeverageVolume.setPrice(50);
        fullBeverageVolume.setCreated(LocalDateTime.now());
        fullBeverageVolume.setModified(LocalDateTime.now());

        OrderBeverage orderBeverage1 = new OrderBeverage();
        orderBeverage1.setId(1L);
        orderBeverage1.setOrder(order);
        orderBeverage1.setBeverageVolume(fullBeverageVolume);
        orderBeverage1.setStatus(OrderBeverageStatus.TAKEN);
        orderBeverage1.setSelectedIce(false);
        orderBeverage1.setCreated(LocalDateTime.now());
        orderBeverage1.setModified(LocalDateTime.now());

        OrderBeverage orderBeverage2 = new OrderBeverage();
        orderBeverage2.setId(2L);
        orderBeverage2.setOrder(order);
        orderBeverage2.setBeverageVolume(fullBeverageVolume);
        orderBeverage2.setStatus(OrderBeverageStatus.READY);
        orderBeverage2.setSelectedIce(false);
        orderBeverage2.setCreated(LocalDateTime.now());
        orderBeverage2.setModified(LocalDateTime.now());

        when(orderBeveragesRepository.findByOrderId(anyLong(), any(Sort.class)))
                .thenReturn(asList(orderBeverage1, orderBeverage2));
        when(orderBeveragesRepository.getById(anyLong())).thenReturn(orderBeverage2);
        when(ordersRepository.getById(anyLong())).thenReturn(order);

        // when
        underTest.processBeverage(order.getId(), orderBeverage2.getId(), ProcessAction.TAKE);

        // then
        verify(processingService).beveragesToStatus(orderBeverage2.getStatus().getNextStatus(), orderBeverage2);
        verify(ordersRepository).save(any(Order.class));
    }

    @Test
    void processBeverageThrowsBeverageCantBeProcessedException() {
        // given
        AppUser user = new AppUser();
        user.setUsername("test_user");
        user.setPassword("password");
        user.setEnabled(true);
        user.setAuthority("USER");

        Order order = new Order();
        order.setId(1L);
        order.setOrderNumber(1001);
        order.setBalance(100);
        order.setActive(true);
        order.setOwner(user);
        order.setCreated(LocalDateTime.now());
        order.setModified(LocalDateTime.now());

        Beverage beverage = new Beverage();
        beverage.setId(1L);
        beverage.setBeverageType(BeverageType.EXPRESSO);
        beverage.setAvailableVolume(0.1);
        beverage.setCreated(LocalDateTime.now());
        beverage.setModified(LocalDateTime.now());

        BeverageVolume fullBeverageVolume = new BeverageVolume();
        fullBeverageVolume.setId(1L);
        fullBeverageVolume.setBeverage(beverage);
        fullBeverageVolume.setVolume(0.5);
        fullBeverageVolume.setPrice(50);
        fullBeverageVolume.setCreated(LocalDateTime.now());
        fullBeverageVolume.setModified(LocalDateTime.now());

        OrderBeverage orderBeverage1 = new OrderBeverage();
        orderBeverage1.setId(1L);
        orderBeverage1.setOrder(order);
        orderBeverage1.setBeverageVolume(fullBeverageVolume);
        orderBeverage1.setStatus(OrderBeverageStatus.READY);
        orderBeverage1.setSelectedIce(false);
        orderBeverage1.setCreated(LocalDateTime.now());
        orderBeverage1.setModified(LocalDateTime.now());

        OrderBeverage orderBeverage2 = new OrderBeverage();
        orderBeverage2.setId(1L);
        orderBeverage2.setOrder(order);
        orderBeverage2.setBeverageVolume(fullBeverageVolume);
        orderBeverage2.setStatus(OrderBeverageStatus.READY_TO_PROCESS);
        orderBeverage2.setSelectedIce(false);
        orderBeverage2.setCreated(LocalDateTime.now());
        orderBeverage2.setModified(LocalDateTime.now());

        when(orderBeveragesRepository.findByOrderId(anyLong(), any(Sort.class)))
                .thenReturn(asList(orderBeverage1, orderBeverage2));

        // when
        Throwable thrown = catchThrowable(() -> underTest.processBeverage(
                order.getId(),
                orderBeverage2.getId(),
                ProcessAction.PROCESS
        ));

        // then
        assertThat(thrown).isInstanceOf(BeverageCantBeProcessedException.class);
        verify(processingService, never()).processBeverage(anyLong());
    }

    @Test
    void processBeverageThrowsIllegalArgumentException() {
        // when
        Throwable thrown = catchThrowable(() -> underTest.processBeverage(null, null, ProcessAction.SUBMIT));

        // then
        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Action type SUBMIT not supported");
        verify(orderBeveragesRepository).findByOrderId(any(), any());
        verify(processingService, never()).processBeverage(any());
        verify(processingService, never()).beveragesToStatus(any(), any());
    }

    @Test
    void canRemoveBeverage() {
        // when
        underTest.removeBeverage(anyLong());

        // then
        verify(orderBeveragesRepository).deleteById(anyLong());
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    void canAddBalanceAndChangeAmountOf10To500() {
        // given
        AppUser user = new AppUser();
        user.setUsername("test_user");
        user.setPassword("password");
        user.setEnabled(true);
        user.setAuthority("USER");

        Order order = new Order();
        order.setId(1L);
        order.setOrderNumber(1001);
        order.setBalance(0);
        order.setActive(true);
        order.setOwner(user);
        order.setCreated(LocalDateTime.now());
        order.setModified(LocalDateTime.now());

        when(ordersRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(ordersRepository.save(any(Order.class))).thenReturn(order);

        // when
        OrderResponseResource result = underTest.addBalance(order.getId(), 10);

        // then
        verify(ordersRepository).save(any(Order.class));
        assertThat(result.getBalance()).isEqualTo(500);
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    void addBalanceThrowsEntityNotFoundException_order() {
        // given
        when(ordersRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when
        Throwable thrown = catchThrowable(() -> underTest.addBalance(1L, 5));

        // then
        assertThat(thrown)
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Order with id='1' not exist.");
        verify(ordersRepository, never()).save(any());
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    void addBalanceThrowsFakeCoinException() {
        // when
        Throwable thrown = catchThrowable(() -> underTest.addBalance(null, 2));

        // then
        assertThat(thrown).isInstanceOf(FakeCoinException.class);
        verify(ordersRepository, never()).save(any());
    }

    @Test
    void canResetBalance() {
        // given
        AppUser user = new AppUser();
        user.setUsername("test_user");
        user.setPassword("password");
        user.setEnabled(true);
        user.setAuthority("USER");

        Order baseOrder = new Order();
        baseOrder.setId(1L);
        baseOrder.setOrderNumber(1001);
        baseOrder.setBalance(100);
        baseOrder.setActive(true);
        baseOrder.setOwner(user);
        baseOrder.setCreated(LocalDateTime.now());
        baseOrder.setModified(LocalDateTime.now());

        Order resetOrder = new Order();
        resetOrder.setId(baseOrder.getId());
        resetOrder.setOrderNumber(baseOrder.getOrderNumber());
        resetOrder.setBalance(0);
        resetOrder.setActive(baseOrder.getActive());
        resetOrder.setOwner(baseOrder.getOwner());
        resetOrder.setCreated(baseOrder.getCreated());
        resetOrder.setModified(baseOrder.getModified());

        when(ordersRepository.findById(anyLong())).thenReturn(Optional.of(baseOrder));
        when(ordersRepository.save(any(Order.class))).thenReturn(resetOrder);

        // when
        OrderResponseResource result = underTest.resetBalance(baseOrder.getId());

        // then
        verify(ordersRepository).save(any(Order.class));
        assertThat(result.getBalance()).isEqualTo(0);
    }

    @Test
    void resetBalanceThrowsEntityNotFoundException_order() {
        // given
        when(ordersRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when
        Throwable thrown = catchThrowable(() -> underTest.resetBalance(1L));

        // then
        assertThat(thrown)
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Order with id='1' not exist.");
        verify(ordersRepository, never()).save(any());
    }

    @Test
    void canSelectIce() {
        // given
        AppUser user = new AppUser();
        user.setUsername("test_user");
        user.setPassword("password");
        user.setEnabled(true);
        user.setAuthority("USER");

        Order order = new Order();
        order.setId(1L);
        order.setOrderNumber(1001);
        order.setBalance(100);
        order.setActive(true);
        order.setOwner(user);
        order.setCreated(LocalDateTime.now());
        order.setModified(LocalDateTime.now());

        Beverage beverage = new Beverage();
        beverage.setId(1L);
        beverage.setBeverageType(BeverageType.EXPRESSO);
        beverage.setAvailableVolume(0.1);
        beverage.setCreated(LocalDateTime.now());
        beverage.setModified(LocalDateTime.now());

        BeverageVolume fullBeverageVolume = new BeverageVolume();
        fullBeverageVolume.setId(1L);
        fullBeverageVolume.setBeverage(beverage);
        fullBeverageVolume.setVolume(0.5);
        fullBeverageVolume.setPrice(50);
        fullBeverageVolume.setCreated(LocalDateTime.now());
        fullBeverageVolume.setModified(LocalDateTime.now());

        OrderBeverage saved = new OrderBeverage();
        saved.setId(1L);
        saved.setOrder(order);
        saved.setBeverageVolume(fullBeverageVolume);
        saved.setStatus(OrderBeverageStatus.SELECTED);
        saved.setSelectedIce(false);
        saved.setCreated(LocalDateTime.now());
        saved.setModified(LocalDateTime.now());

        when(orderBeveragesRepository.getById(anyLong())).thenReturn(saved);
        when(orderBeveragesRepository.save(any(OrderBeverage.class))).thenReturn(saved);

        // when
        OrderBeverageResponseResource result = underTest.selectIce(saved.getId(), true);

        // then
        verify(orderBeveragesRepository).save(any(OrderBeverage.class));
        assertThat(result.isSelectedIce()).isTrue();
    }
}