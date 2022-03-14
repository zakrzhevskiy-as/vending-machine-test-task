package com.sbt.pprb.qa.test_task;

import com.sbt.pprb.qa.test_task.controller.api.ApplicationInfoController;
import com.sbt.pprb.qa.test_task.controller.api.BeverageController;
import com.sbt.pprb.qa.test_task.controller.api.OrderController;
import com.sbt.pprb.qa.test_task.controller.mvc.HomeController;
import com.sbt.pprb.qa.test_task.controller.mvc.RegisterController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = VendingMachineApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
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
	void contextLoads() {
		assertThat(homeController)
				.withFailMessage("Home controller not initialized")
				.isNotNull();
		assertThat(registerController)
				.withFailMessage("Register controller not initialized")
				.isNotNull();
		assertThat(applicationInfoController)
				.withFailMessage("Application info controller not initialized")
				.isNotNull();
		assertThat(beverageController)
				.withFailMessage("Beverage controller not initialized")
				.isNotNull();
		assertThat(orderController)
				.withFailMessage("Order controller not initialized")
				.isNotNull();
	}

}
