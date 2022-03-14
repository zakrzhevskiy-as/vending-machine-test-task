package com.sbt.pprb.qa.test_task.repository;

import com.sbt.pprb.qa.test_task.model.dto.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.util.comparator.Comparators;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.sbt.pprb.qa.test_task.model.dto.BeverageType.SLURM;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OrderBeveragesRepositoryTest {

    @Autowired
    private OrderBeveragesRepository underTest;
    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private BeveragesRepository beveragesRepository;
    @Autowired
    private BeverageVolumesRepository beverageVolumesRepository;

    private AppUser testUser;
    private Order testOrder;
    private final List<OrderBeverage> orderBeverages = new ArrayList<>();

    @BeforeEach
    void setUp() throws InterruptedException {
        AppUser user = new AppUser();
        user.setUsername("test_user");
        user.setPassword("password");
        user.setEnabled(true);
        user.setAuthority("USER");
        this.testUser = usersRepository.save(user);

        Order order = new Order();
        order.setOrderNumber(ordersRepository.getNextOrderNumber());
        order.setOwner(this.testUser);
        order.setActive(true);
        order.setBalance(100);
        this.testOrder = ordersRepository.saveAndFlush(order);

        List<Beverage> beverages = beveragesRepository.findAll()
                .stream()
                .filter(beverage -> SLURM.equals(beverage.getBeverageType()))
                .collect(Collectors.toList());

        for (Beverage beverage : beverages) {
            List<BeverageVolume> beverageVolumes = beverageVolumesRepository.findBeverageVolumeByBeverage(beverage);
            for (BeverageVolume beverageVolume : beverageVolumes) {
                OrderBeverage orderBeverage = new OrderBeverage();
                orderBeverage.setOrder(this.testOrder);
                orderBeverage.setStatus(OrderBeverageStatus.SELECTED);
                orderBeverage.setBeverageVolume(beverageVolume);
                orderBeverage.setSelectedIce(false);

                this.orderBeverages.add(this.underTest.save(orderBeverage));
                Thread.sleep(2L);
            }
        }
    }

    @AfterEach
    void tearDown() {
        ordersRepository.delete(testOrder);
        usersRepository.delete(testUser);
    }

    @Test
    void itShouldFindBeveragesByExistingOrderIdUnsorted() {
        // when
        List<OrderBeverage> result = underTest.findByOrderId(this.testOrder.getId(), Sort.unsorted());

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).containsAll(this.orderBeverages);
    }

    @Test
    void itShouldNotFindBeveragesByNotExistingOrderIdSortedByCreationDateAsc() {
        // given
        List<OrderBeverage> sorted = new ArrayList<>(this.orderBeverages);
        sorted.sort((obj1, obj2) -> Comparators.comparable().compare(obj1.getCreated(), obj2.getCreated()));

        // when
        Sort sort = Sort.by(Sort.Direction.ASC, "created");
        List<OrderBeverage> result = underTest.findByOrderId(this.testOrder.getId(), sort);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).containsExactly(sorted.toArray(new OrderBeverage[]{}));
    }

    @Test
    void itShouldNotFindBeveragesByNotExistingOrderIdSortedByCreationDateDesc() {
        // given
        List<OrderBeverage> sorted = new ArrayList<>(this.orderBeverages);
        sorted.sort((obj1, obj2) -> Comparators.comparable().reversed().compare(obj1.getCreated(), obj2.getCreated()));

        // when
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        List<OrderBeverage> result = underTest.findByOrderId(this.testOrder.getId(), sort);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).containsExactly(sorted.toArray(new OrderBeverage[]{}));
    }
}