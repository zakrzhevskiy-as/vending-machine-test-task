package com.sbt.pprb.qa.test_task.repository;

import com.sbt.pprb.qa.test_task.model.dto.*;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.util.comparator.Comparators;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.sbt.pprb.qa.test_task.model.dto.BeverageType.SLURM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

@DataJpaTest
class OrdersRepositoryTest {

    @Autowired
    private OrdersRepository underTest;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private BeveragesRepository beveragesRepository;
    @Autowired
    private BeverageVolumesRepository beverageVolumesRepository;
    @Autowired
    private OrderBeveragesRepository orderBeveragesRepository;

    private AppUser testUser;
    private Order testActiveOrder;
    private final List<Order> testFinishedOrders = new ArrayList<>();

    @AfterEach
    void tearDown() {
        if (testActiveOrder != null) {
            underTest.delete(testActiveOrder);
            testActiveOrder = null;
        }
        if (!testFinishedOrders.isEmpty()) {
            underTest.deleteAll(testFinishedOrders);
            testFinishedOrders.clear();
        }
        if (testUser != null) {
            usersRepository.delete(testUser);
            testUser = null;
        }
    }

    @Test
    void itShouldGetCorrectNextOrderNumberByOne() {
        // given
        Integer previousOrderNumber = underTest.getNextOrderNumber();

        // when
        Integer result = underTest.getNextOrderNumber();

        // then
        assertThat(result).isEqualTo(previousOrderNumber + 1);
    }

    @Test
    void itShouldFindOrderByOwnerAndActive() {
        // given
        AppUser user = new AppUser();
        user.setUsername("test_user");
        user.setPassword("password");
        user.setEnabled(true);
        user.setAuthority("USER");
        testUser = usersRepository.save(user);

        Order order = new Order();
        order.setOrderNumber(underTest.getNextOrderNumber());
        order.setActive(true);
        order.setBalance(100);
        order.setOwner(user);
        testActiveOrder = underTest.save(order);

        // when
        List<Order> result = underTest.findByOwnerAndActive(testUser, true, Sort.unsorted());

        // then
        assertThat(result).containsExactly(order);
    }

    @Test
    void itShouldNotFindOrderByOwnerAndActive() {
        // given
        AppUser user = new AppUser();
        user.setUsername("test_user");
        user.setPassword("password");
        user.setEnabled(true);
        user.setAuthority("USER");
        testUser = usersRepository.save(user);

        // when
        List<Order> result = underTest.findByOwnerAndActive(testUser, true, Sort.unsorted());

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void itShouldFindOrdersByOwnerAndNotActiveAndUnsorted() {
        // given
        AppUser user = new AppUser();
        user.setUsername("test_user");
        user.setPassword("password");
        user.setEnabled(true);
        user.setAuthority("USER");
        testUser = usersRepository.save(user);

        for (int i = 0; i < 5; i++) {
            Order order = new Order();
            order.setOrderNumber(underTest.getNextOrderNumber());
            order.setActive(false);
            order.setBalance(100);
            order.setOwner(user);
            testFinishedOrders.add(underTest.save(order));
        }

        // when
        List<Order> result = underTest.findByOwnerAndActive(testUser, false, Sort.unsorted());

        // then
        assertThat(result).containsAll(testFinishedOrders);
    }

    @Test
    void itShouldFindOrdersByOwnerAndNotActiveAndSortedByCreatedAsc() throws InterruptedException {
        // given
        AppUser user = new AppUser();
        user.setUsername("test_user");
        user.setPassword("password");
        user.setEnabled(true);
        user.setAuthority("USER");
        testUser = usersRepository.save(user);

        for (int i = 0; i < 5; i++) {
            Order order = new Order();
            order.setOrderNumber(underTest.getNextOrderNumber());
            order.setActive(false);
            order.setBalance(100);
            order.setOwner(user);
            testFinishedOrders.add(underTest.save(order));
            Thread.sleep(5L);
        }
        testFinishedOrders.sort((obj1, obj2) -> Comparators.comparable().compare(obj1.getCreated(), obj2.getCreated()));

        // when
        Sort sort = Sort.by(Sort.Direction.ASC, "created");
        List<Order> result = underTest.findByOwnerAndActive(testUser, false, sort);

        // then
        assertThat(result).containsExactly(testFinishedOrders.toArray(new Order[]{}));
    }

    @Test
    void itShouldFindOrdersByOwnerAndNotActiveAndSortedByCreatedDesc() throws InterruptedException {
        // given
        AppUser user = new AppUser();
        user.setUsername("test_user");
        user.setPassword("password");
        user.setEnabled(true);
        user.setAuthority("USER");
        testUser = usersRepository.save(user);

        for (int i = 0; i < 5; i++) {
            Order order = new Order();
            order.setOrderNumber(underTest.getNextOrderNumber());
            order.setActive(false);
            order.setBalance(100);
            order.setOwner(user);
            testFinishedOrders.add(underTest.save(order));
            Thread.sleep(5L);
        }
        testFinishedOrders.sort((obj1, obj2) -> Comparators.comparable().reversed().compare(obj1.getCreated(), obj2.getCreated()));

        // when
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        List<Order> result = underTest.findByOwnerAndActive(testUser, false, sort);

        // then
        assertThat(result).containsExactly(testFinishedOrders.toArray(new Order[]{}));
    }

    @Test
    void itShouldDeleteActiveOrderByOwner() {
        // given
        AppUser user = new AppUser();
        user.setUsername("test_user");
        user.setPassword("password");
        user.setEnabled(true);
        user.setAuthority("USER");
        testUser = usersRepository.save(user);

        Order order = new Order();
        order.setOrderNumber(underTest.getNextOrderNumber());
        order.setActive(true);
        order.setBalance(100);
        order.setOwner(user);
        testActiveOrder = underTest.save(order);

        // when
        underTest.deleteAllByOwnerAndActive(testUser, true);

        // then
        Optional<Order> result = underTest.findById(testActiveOrder.getId());
        assertThat(result).isEmpty();
    }

    @Test
    void itShouldNotDeleteNotActiveOrdersByOwner() {
        // given
        AppUser user = new AppUser();
        user.setUsername("test_user");
        user.setPassword("password");
        user.setEnabled(true);
        user.setAuthority("USER");
        testUser = usersRepository.save(user);

        for (int i = 0; i < 5; i++) {
            Order order = new Order();
            order.setOrderNumber(underTest.getNextOrderNumber());
            order.setActive(false);
            order.setBalance(100);
            order.setOwner(user);
            order = underTest.save(order);

            List<Beverage> beverages = beveragesRepository.findAll()
                    .stream()
                    .filter(beverage -> SLURM.equals(beverage.getBeverageType()))
                    .collect(Collectors.toList());

            for (Beverage beverage : beverages) {
                List<BeverageVolume> beverageVolumes = beverageVolumesRepository.findBeverageVolumeByBeverage(beverage);
                for (BeverageVolume beverageVolume : beverageVolumes) {
                    OrderBeverage orderBeverage = new OrderBeverage();
                    orderBeverage.setOrder(order);
                    orderBeverage.setStatus(OrderBeverageStatus.TAKEN);
                    orderBeverage.setBeverageVolume(beverageVolume);
                    orderBeverage.setSelectedIce(false);

                    orderBeveragesRepository.save(orderBeverage);
                }
            }

            testFinishedOrders.add(order);
        }

        // when
        Throwable thrown = catchThrowable(() -> {
            underTest.deleteAllByOwnerAndActive(testUser, false);
            underTest.flush();
        });

        // then
        assertThat(thrown)
                .hasCauseInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("constraint [\"FK_ORDERS_BEVERAGES_ORDERS: VENDING_MACHINE.ORDERS_BEVERAGES FOREIGN KEY(ORDER_ID) REFERENCES VENDING_MACHINE.ORDERS(ID)");
    }
}