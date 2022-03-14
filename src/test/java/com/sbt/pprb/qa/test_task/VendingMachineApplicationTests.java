package com.sbt.pprb.qa.test_task;

import com.sbt.pprb.qa.test_task.controller.api.ApplicationInfoController;
import com.sbt.pprb.qa.test_task.controller.api.BeverageController;
import com.sbt.pprb.qa.test_task.controller.api.OrderController;
import com.sbt.pprb.qa.test_task.controller.mvc.HomeController;
import com.sbt.pprb.qa.test_task.controller.mvc.RegisterController;
import io.qameta.allure.Epic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = VendingMachineApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Epic("Unit-тесты контекста приложения")
@DisplayName("Тесты проверки создания контроллеров")
class VendingMachineApplicationTests {

	@Autowired
	private HomeController homeController;
	@Autowired
	private ApplicationInfoController applicationInfoController;
	@Autowired
	private BeverageController beverageController;
	@Autowired
	private OrderController orderController;
	@Autowired
	private RegisterController registerController;

	@Test
	void homeControllerLoads() {
		assertThat(homeController)
				.withFailMessage("Home controller not initialized")
				.isNotNull();
	}

	@Test
	void applicationInfoControllerLoads() {
		assertThat(applicationInfoController)
				.withFailMessage("Application info controller not initialized")
				.isNotNull();
	}

	@Test
	void beverageControllerLoads() {
		assertThat(beverageController)
				.withFailMessage("Beverage controller not initialized")
				.isNotNull();

	}

	@Test
	void orderControllerLoads() {
		assertThat(orderController)
				.withFailMessage("Order controller not initialized")
				.isNotNull();
	}

	@Test
	void registerControllerLoads() {
		assertThat(registerController)
				.withFailMessage("Register controller not initialized")
				.isNotNull();
	}

}
