package com.sbt.pprb.qa.test_task.controller.api;

import com.sbt.pprb.qa.test_task.controller.ControllerTestContext;
import com.sbt.pprb.qa.test_task.model.dto.AppUser;
import com.sbt.pprb.qa.test_task.model.dto.BeverageType;
import com.sbt.pprb.qa.test_task.model.dto.Order;
import com.sbt.pprb.qa.test_task.model.dto.OrderBeverageStatus;
import com.sbt.pprb.qa.test_task.model.exception.InternalException;
import com.sbt.pprb.qa.test_task.model.response.BeverageVolumeResponseResource;
import com.sbt.pprb.qa.test_task.model.response.OrderBeverageResponseResource;
import com.sbt.pprb.qa.test_task.model.response.OrderResponseResource;
import com.sbt.pprb.qa.test_task.service.OrderService;
import com.sbt.pprb.qa.test_task.service.UserService;
import io.qameta.allure.Epic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Epic("Integration-тесты контроллеров")
@DisplayName("Тесты контроллера апи OrderController")
class OrderControllerTest extends ControllerTestContext {

    @MockBean
    private OrderService orderService;
    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    @Test
    void itShouldReturnOrdersList() throws Exception {
        // Given
        AppUser user = new AppUser();
        user.setId(1L);
        user.setUsername(USERNAME);
        user.setPassword("password");
        user.setAuthority("USER");
        user.setEnabled(true);
        user.setCreated(LocalDateTime.now());
        user.setModified(LocalDateTime.now());

        BeverageVolumeResponseResource volume = new BeverageVolumeResponseResource();
        volume.setId(1L);
        volume.setVolume(0.5);
        volume.setPrice(100);

        OrderBeverageResponseResource beverage = new OrderBeverageResponseResource();
        beverage.setId(1L);
        beverage.setStatus(OrderBeverageStatus.SELECTED);
        beverage.setBeverageType(BeverageType.SLURM);
        beverage.setBeverageVolume(volume);
        beverage.setSelectedIce(false);

        OrderResponseResource order = new OrderResponseResource();
        order.setId(1L);
        order.setOrderNumber(1001);
        order.setBalance(100);
        order.setTotalCost(50);
        order.setOrderBeverages(singletonList(beverage));
        order.setCreated(LocalDateTime.now());

        List<OrderResponseResource> activeOrders = singletonList(order);
        when(orderService.getOrders(any(), anyBoolean())).thenReturn(activeOrders);
        when(userService.getUser(eq(USERNAME))).thenReturn(Optional.of(user));

        // When
        MockHttpServletRequestBuilder request = get("/api/v1/orders")
                .with(auth)
                .param("active", "true");

        // Then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(not(emptyList())))
                .andExpect(jsonPath("$[0]").value(notNullValue(OrderResponseResource.class)))
                .andExpect(jsonPath("$[0].id").value(activeOrders.get(0).getId()))
                .andExpect(jsonPath("$[0].orderNumber").value(activeOrders.get(0).getOrderNumber()))
                .andExpect(jsonPath("$[0].balance").value(activeOrders.get(0).getBalance()))
                .andExpect(jsonPath("$[0].totalCost").value(activeOrders.get(0).getTotalCost()))
                .andExpect(jsonPath("$[0].created").value(formatter.format(activeOrders.get(0).getCreated())))
                .andExpect(jsonPath("$[0].orderBeverages").value(not(emptyList())))
                .andExpect(jsonPath("$[0].orderBeverages[0]").value(not(emptyList())))
                .andExpect(jsonPath("$[0].orderBeverages[0].id").value(activeOrders.get(0).getOrderBeverages().get(0).getId()))
                .andExpect(jsonPath("$[0].orderBeverages[0].status").value(activeOrders.get(0).getOrderBeverages().get(0).getStatus().name()))
                .andExpect(jsonPath("$[0].orderBeverages[0].beverageType").value(activeOrders.get(0).getOrderBeverages().get(0).getBeverageType().getType()))
                .andExpect(jsonPath("$[0].orderBeverages[0].selectedIce").value(activeOrders.get(0).getOrderBeverages().get(0).isSelectedIce()))
                .andExpect(jsonPath("$[0].orderBeverages[0].beverageVolume").value(notNullValue(BeverageVolumeResponseResource.class)))
                .andExpect(jsonPath("$[0].orderBeverages[0].beverageVolume.id").value(activeOrders.get(0).getOrderBeverages().get(0).getBeverageVolume().getId()))
                .andExpect(jsonPath("$[0].orderBeverages[0].beverageVolume.volume").value(activeOrders.get(0).getOrderBeverages().get(0).getBeverageVolume().getVolume()))
                .andExpect(jsonPath("$[0].orderBeverages[0].beverageVolume.price").value(activeOrders.get(0).getOrderBeverages().get(0).getBeverageVolume().getPrice()));
    }

    @Test
    void itShouldReturnUnauthorizedOnOrdersListRequest() throws Exception {
        // When
        MockHttpServletRequestBuilder request = get("/api/v1/orders")
                .param("active", "true");

        // Then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void itShouldNotDeleteFinishedOrders() throws Exception {
        // Given
        AppUser user = new AppUser();
        user.setId(1L);
        user.setUsername(USERNAME);
        user.setPassword("password");
        user.setAuthority("USER");
        user.setEnabled(true);
        user.setCreated(LocalDateTime.now());
        user.setModified(LocalDateTime.now());

        doThrow(DataIntegrityViolationException.class)
                .when(orderService).deleteFinished(any());
        when(userService.getUser(eq(USERNAME))).thenReturn(Optional.of(user));

        // When
        MockHttpServletRequestBuilder request = delete("/api/v1/orders").with(auth);

        // Then
        MvcResult mvcResult = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andReturn();

        Optional<InternalException> resolvedException = Optional.ofNullable((InternalException) mvcResult.getResolvedException());
        resolvedException.ifPresent(exception -> assertThat(exception)
                .withFailMessage("Текст ошибки не соответствует ожиданию")
                .hasMessage("org.springframework.dao.DataIntegrityViolationException")
        );
    }

    @Test
    void itShouldReturnUnauthorizedOnOrdersDeleteRequest() throws Exception {
        // When
        MockHttpServletRequestBuilder request = delete("/api/v1/orders");

        // Then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void itShouldReturnOrder() throws Exception {
        // Given
        AppUser user = new AppUser();
        user.setId(1L);
        user.setUsername(USERNAME);
        user.setPassword("password");
        user.setAuthority("USER");
        user.setEnabled(true);
        user.setCreated(LocalDateTime.now());
        user.setModified(LocalDateTime.now());

        BeverageVolumeResponseResource volume = new BeverageVolumeResponseResource();
        volume.setId(1L);
        volume.setVolume(0.5);
        volume.setPrice(100);

        OrderBeverageResponseResource beverage = new OrderBeverageResponseResource();
        beverage.setId(1L);
        beverage.setStatus(OrderBeverageStatus.SELECTED);
        beverage.setBeverageType(BeverageType.SLURM);
        beverage.setBeverageVolume(volume);
        beverage.setSelectedIce(false);

        OrderResponseResource order = new OrderResponseResource();
        order.setId(1L);
        order.setOrderNumber(1001);
        order.setBalance(100);
        order.setTotalCost(50);
        order.setOrderBeverages(singletonList(beverage));
        order.setCreated(LocalDateTime.now());

        when(orderService.getOrder(anyLong())).thenReturn(order);

        // When
        MockHttpServletRequestBuilder request = get("/api/v1/orders/1").with(auth);

        // Then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(notNullValue(OrderResponseResource.class)))
                .andExpect(jsonPath("$.id").value(order.getId()))
                .andExpect(jsonPath("$.orderNumber").value(order.getOrderNumber()))
                .andExpect(jsonPath("$.balance").value(order.getBalance()))
                .andExpect(jsonPath("$.totalCost").value(order.getTotalCost()))
                .andExpect(jsonPath("$.created").value(formatter.format(order.getCreated())))
                .andExpect(jsonPath("$.orderBeverages").value(not(emptyList())))
                .andExpect(jsonPath("$.orderBeverages[0]").value(not(emptyList())))
                .andExpect(jsonPath("$.orderBeverages[0].id").value(order.getOrderBeverages().get(0).getId()))
                .andExpect(jsonPath("$.orderBeverages[0].status").value(order.getOrderBeverages().get(0).getStatus().name()))
                .andExpect(jsonPath("$.orderBeverages[0].beverageType").value(order.getOrderBeverages().get(0).getBeverageType().getType()))
                .andExpect(jsonPath("$.orderBeverages[0].selectedIce").value(order.getOrderBeverages().get(0).isSelectedIce()))
                .andExpect(jsonPath("$.orderBeverages[0].beverageVolume").value(notNullValue(BeverageVolumeResponseResource.class)))
                .andExpect(jsonPath("$.orderBeverages[0].beverageVolume.id").value(order.getOrderBeverages().get(0).getBeverageVolume().getId()))
                .andExpect(jsonPath("$.orderBeverages[0].beverageVolume.volume").value(order.getOrderBeverages().get(0).getBeverageVolume().getVolume()))
                .andExpect(jsonPath("$.orderBeverages[0].beverageVolume.price").value(order.getOrderBeverages().get(0).getBeverageVolume().getPrice()));
    }

    @Test
    void itShouldReturnUnauthorizedOnOrderRequest() throws Exception {
        // When
        MockHttpServletRequestBuilder request = get("/api/v1/orders/1");

        // Then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void itShouldDeleteOrder() throws Exception {
        // Given
        doNothing().when(orderService).deleteOrder(anyLong());

        // When
        MockHttpServletRequestBuilder request = delete("/api/v1/orders/1").with(auth);

        // Then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void itShouldReturnUnauthorizedOnOrderDeleteRequest() throws Exception {
        // When
        MockHttpServletRequestBuilder request = delete("/api/v1/orders/1");

        // Then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void itShouldCreateOrder() throws Exception {
        // Given
        AppUser user = new AppUser();
        user.setId(1L);
        user.setUsername(USERNAME);
        user.setPassword("password");
        user.setAuthority("USER");
        user.setEnabled(true);
        user.setCreated(LocalDateTime.now());
        user.setModified(LocalDateTime.now());

        Order order = new Order();
        order.setId(1L);
        order.setActive(true);
        order.setOrderNumber(1001);
        order.setBalance(0);
        order.setOwner(user);
        order.setCreated(LocalDateTime.now());
        order.setModified(LocalDateTime.now());

        OrderResponseResource responseResource = new OrderResponseResource();
        responseResource.setId(order.getId());
        responseResource.setOrderNumber(order.getOrderNumber());
        responseResource.setBalance(0);
        responseResource.setTotalCost(0);
        responseResource.setCreated(order.getCreated());

        when(userService.getUser(anyString())).thenReturn(Optional.of(user));
        when(orderService.createOrder(any())).thenReturn(order);
        when(orderService.getOrderResponseResource(any())).thenReturn(responseResource);

        // When
        MockHttpServletRequestBuilder request = post("/api/v1/orders").with(auth);

        // Then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(notNullValue(OrderResponseResource.class)))
                .andExpect(jsonPath("$.id").value(responseResource.getId()))
                .andExpect(jsonPath("$.orderNumber").value(responseResource.getOrderNumber()))
                .andExpect(jsonPath("$.balance").value(responseResource.getBalance()))
                .andExpect(jsonPath("$.totalCost").value(responseResource.getTotalCost()))
                .andExpect(jsonPath("$.created").value(formatter.format(responseResource.getCreated())))
                .andExpect(jsonPath("$.orderBeverages").value(nullValue()));
    }

    @Test
    void itShouldReturnUnauthorizedOnOrderCreateRequest() throws Exception {
        // When
        MockHttpServletRequestBuilder request = post("/api/v1/orders");

        // Then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void itShouldAddBeverage() throws Exception {
        // Given
        BeverageVolumeResponseResource volume = new BeverageVolumeResponseResource();
        volume.setId(1L);
        volume.setVolume(0.5);
        volume.setPrice(100);

        OrderBeverageResponseResource beverage = new OrderBeverageResponseResource();
        beverage.setId(1L);
        beverage.setStatus(OrderBeverageStatus.SELECTED);
        beverage.setBeverageType(BeverageType.SLURM);
        beverage.setBeverageVolume(volume);
        beverage.setSelectedIce(false);

        when(orderService.addBeverage(anyLong(), any())).thenReturn(beverage);

        // When
        MockHttpServletRequestBuilder request = put("/api/v1/orders/1/beverages")
                .with(auth)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"beverageVolume\": { \"id\": 1 } }");

        // Then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(notNullValue(OrderBeverageResponseResource.class)))
                .andExpect(jsonPath("$.id").value(beverage.getId()))
                .andExpect(jsonPath("$.status").value(beverage.getStatus().name()))
                .andExpect(jsonPath("$.beverageType").value(beverage.getBeverageType().getType()))
                .andExpect(jsonPath("$.selectedIce").value(beverage.isSelectedIce()))
                .andExpect(jsonPath("$.beverageVolume").value(notNullValue(BeverageVolumeResponseResource.class)))
                .andExpect(jsonPath("$.beverageVolume.id").value(beverage.getBeverageVolume().getId()))
                .andExpect(jsonPath("$.beverageVolume.volume").value(beverage.getBeverageVolume().getVolume()))
                .andExpect(jsonPath("$.beverageVolume.price").value(beverage.getBeverageVolume().getPrice()));
    }

    @Test
    void itShouldReturnUnauthorizedOnAddBeverageRequest() throws Exception {
        // When
        MockHttpServletRequestBuilder request = put("/api/v1/orders/1/beverages");

        // Then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void itShouldRemoveBeverage() throws Exception {
        // Given
        doNothing().when(orderService).removeBeverage(anyLong());

        // When
        MockHttpServletRequestBuilder request = delete("/api/v1/orders/beverages/1").with(auth);

        // Then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void itShouldReturnUnauthorizedOnRemoveBeverageRequest() throws Exception {
        // When
        MockHttpServletRequestBuilder request = delete("/api/v1/orders/beverages/1");

        // Then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void itShouldProcessBeverage() throws Exception {
        // Given
        BeverageVolumeResponseResource volume = new BeverageVolumeResponseResource();
        volume.setId(1L);
        volume.setVolume(0.5);
        volume.setPrice(100);

        OrderBeverageResponseResource beverage = new OrderBeverageResponseResource();
        beverage.setId(1L);
        beverage.setStatus(OrderBeverageStatus.READY_TO_PROCESS);
        beverage.setBeverageType(BeverageType.SLURM);
        beverage.setBeverageVolume(volume);
        beverage.setSelectedIce(false);

        List<OrderBeverageResponseResource> beverages = singletonList(beverage);
        when(orderService.submitOrder(anyLong())).thenReturn(beverages);

        // When
        MockHttpServletRequestBuilder request = post("/api/v1/orders/1")
                .with(auth)
                .param("action", "SUBMIT");

        // Then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(not(emptyList())))
                .andExpect(jsonPath("$[0]").value(notNullValue(OrderBeverageResponseResource.class)))
                .andExpect(jsonPath("$[0].id").value(beverage.getId()))
                .andExpect(jsonPath("$[0].status").value(beverage.getStatus().name()))
                .andExpect(jsonPath("$[0].beverageType").value(beverage.getBeverageType().getType()))
                .andExpect(jsonPath("$[0].selectedIce").value(beverage.isSelectedIce()))
                .andExpect(jsonPath("$[0].beverageVolume").value(notNullValue(BeverageVolumeResponseResource.class)))
                .andExpect(jsonPath("$[0].beverageVolume.id").value(beverage.getBeverageVolume().getId()))
                .andExpect(jsonPath("$[0].beverageVolume.volume").value(beverage.getBeverageVolume().getVolume()))
                .andExpect(jsonPath("$[0].beverageVolume.price").value(beverage.getBeverageVolume().getPrice()));
    }

    @Test
    void itShouldReturnUnauthorizedOnProcessBeverageRequest() throws Exception {
        // When
        MockHttpServletRequestBuilder request = post("/api/v1/orders/1")
                .param("action", "SUBMIT");

        // Then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void itShouldAddBalance() throws Exception {
        // Given
        OrderResponseResource order = new OrderResponseResource();
        order.setId(1L);
        order.setOrderNumber(1001);
        order.setBalance(10);
        order.setTotalCost(0);
        order.setCreated(LocalDateTime.now());

        when(orderService.addBalance(anyLong(), anyInt())).thenReturn(order);

        // When
        MockHttpServletRequestBuilder request = put("/api/v1/orders/1/balance")
                .with(auth)
                .param("amount", "10");

        // Then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(notNullValue(OrderResponseResource.class)))
                .andExpect(jsonPath("$.id").value(order.getId()))
                .andExpect(jsonPath("$.orderNumber").value(order.getOrderNumber()))
                .andExpect(jsonPath("$.balance").value(order.getBalance()))
                .andExpect(jsonPath("$.totalCost").value(order.getTotalCost()))
                .andExpect(jsonPath("$.created").value(formatter.format(order.getCreated())))
                .andExpect(jsonPath("$.orderBeverages").value(nullValue()));
    }

    @Test
    void itShouldReturnUnauthorizedOnAddBalanceRequest() throws Exception {
        // When
        MockHttpServletRequestBuilder request = put("/api/v1/orders/1/balance")
                .param("amount", "10");

        // Then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void itShouldResetBalance() throws Exception {
        // Given
        OrderResponseResource order = new OrderResponseResource();
        order.setId(1L);
        order.setOrderNumber(1001);
        order.setBalance(0);
        order.setTotalCost(0);
        order.setCreated(LocalDateTime.now());

        when(orderService.resetBalance(anyLong())).thenReturn(order);

        // When
        MockHttpServletRequestBuilder request = delete("/api/v1/orders/1/balance").with(auth);

        // Then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(notNullValue(OrderResponseResource.class)))
                .andExpect(jsonPath("$.id").value(order.getId()))
                .andExpect(jsonPath("$.orderNumber").value(order.getOrderNumber()))
                .andExpect(jsonPath("$.balance").value(order.getBalance()))
                .andExpect(jsonPath("$.totalCost").value(order.getTotalCost()))
                .andExpect(jsonPath("$.created").value(formatter.format(order.getCreated())))
                .andExpect(jsonPath("$.orderBeverages").value(nullValue()));
    }

    @Test
    void itShouldReturnUnauthorizedOnResetBalanceRequest() throws Exception {
        // When
        MockHttpServletRequestBuilder request = delete("/api/v1/orders/1/balance");

        // Then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void itShouldSelectIce() throws Exception {
        BeverageVolumeResponseResource volume = new BeverageVolumeResponseResource();
        volume.setId(1L);
        volume.setVolume(0.5);
        volume.setPrice(100);

        OrderBeverageResponseResource beverage = new OrderBeverageResponseResource();
        beverage.setId(1L);
        beverage.setStatus(OrderBeverageStatus.READY_TO_PROCESS);
        beverage.setBeverageType(BeverageType.SLURM);
        beverage.setBeverageVolume(volume);
        beverage.setSelectedIce(true);

        when(orderService.selectIce(anyLong(), anyBoolean())).thenReturn(beverage);

        // When
        MockHttpServletRequestBuilder request = patch("/api/v1/orders/beverages/1/ice")
                .with(auth)
                .param("withIce", "true");

        // Then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(notNullValue(OrderBeverageResponseResource.class)))
                .andExpect(jsonPath("$.id").value(beverage.getId()))
                .andExpect(jsonPath("$.status").value(beverage.getStatus().name()))
                .andExpect(jsonPath("$.beverageType").value(beverage.getBeverageType().getType()))
                .andExpect(jsonPath("$.selectedIce").value(beverage.isSelectedIce()))
                .andExpect(jsonPath("$.beverageVolume").value(notNullValue(BeverageVolumeResponseResource.class)))
                .andExpect(jsonPath("$.beverageVolume.id").value(beverage.getBeverageVolume().getId()))
                .andExpect(jsonPath("$.beverageVolume.volume").value(beverage.getBeverageVolume().getVolume()))
                .andExpect(jsonPath("$.beverageVolume.price").value(beverage.getBeverageVolume().getPrice()));
    }

    @Test
    void itShouldReturnUnauthorizedOnSelectIceRequest() throws Exception {
        // When
        MockHttpServletRequestBuilder request = patch("/api/v1/orders/beverages/1/ice")
                .param("withIce", "true");

        // Then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}