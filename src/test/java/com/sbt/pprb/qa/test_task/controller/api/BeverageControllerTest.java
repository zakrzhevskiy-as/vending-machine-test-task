package com.sbt.pprb.qa.test_task.controller.api;

import com.sbt.pprb.qa.test_task.CommonTestContext;
import com.sbt.pprb.qa.test_task.model.dto.Beverage;
import com.sbt.pprb.qa.test_task.repository.BeveragesRepository;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@SpringBootTest(classes = VendingMachineApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
//@AutoConfigureMockMvc
class BeverageControllerTest extends CommonTestContext {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private BeveragesRepository repository;

//    @Test
    @Disabled
    void getVolumes_endpoint_test() throws Exception {
        MockHttpServletRequestBuilder request = get("/api/beverages/volumes")
                .with(auth)
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", not(emptyArray())))
                .andExpect(jsonPath(
                        "$[0]",
                        allOf(
                                hasKey("id"),
                                hasKey("availableVolume"),
                                hasKey("beverageType"),
                                hasKey("beverageVolumes")
                        )
                ))
                .andExpect(jsonPath("$[0].id", allOf(notNullValue(), instanceOf(Integer.class))))
                .andExpect(jsonPath("$[0].availableVolume", allOf(notNullValue(), instanceOf(Double.class))))
                .andExpect(jsonPath(
                        "$[0].beverageType",
                        allOf(notNullValue(), anyOf(is("Экспрессо"), is("Nuka-Cola"), is("Slurm"))))
                )
                .andExpect(jsonPath("$[0].beverageVolumes", not(emptyArray())))
                .andExpect(jsonPath(
                        "$[0].beverageVolumes[0]",
                        allOf(
                                hasKey("id"),
                                hasKey("volume"),
                                hasKey("price")
                        )
                ))
                .andExpect(jsonPath("$[0].beverageVolumes[0].id", allOf(notNullValue(), instanceOf(Integer.class))))
                .andExpect(jsonPath("$[0].beverageVolumes[0].volume", allOf(notNullValue(), instanceOf(Double.class))))
                .andExpect(jsonPath("$[0].beverageVolumes[0].price", allOf(notNullValue(), instanceOf(Integer.class))));
    }

//    @Test
    @Disabled
    void addAvailableVolume_endpoint_test() throws Exception {
        Optional<Beverage> optionalBeverage = repository.findAll().stream().findAny();
        Assumptions.assumeTrue(optionalBeverage.isPresent(), "No beverages in DB");

        Beverage beverage = optionalBeverage.get();
        Double volume = 2.5;

        MockHttpServletRequestBuilder request = put("/api/beverages/" + beverage.getId())
                .with(auth)
                .param("volume", String.valueOf(volume));

        mvc.perform(request).andExpect(status().isOk());

        Beverage newBeverage = repository.getById(beverage.getId());

        assertThat(
                "Wrong value added to beverage available volume",
                newBeverage.getAvailableVolume() - beverage.getAvailableVolume(),
                is(volume)
        );
    }
}