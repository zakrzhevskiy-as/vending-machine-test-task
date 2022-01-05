package com.sbt.pprb.qa.test_task;

import com.sbt.pprb.qa.test_task.controller.mvc.HomeController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(classes = VendingMachineApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class VendingMachineApplicationTests extends CommonTestContext{

	@Autowired
	private HomeController controller;

	@Test
	void contextLoads() {
		assertThat("Application context not initialized", controller, notNullValue());
	}

}
