package com.sbt.pprb.qa.test_task.controller.api;

import com.sbt.pprb.qa.test_task.controller.ControllerTestContext;
import com.sbt.pprb.qa.test_task.model.dto.BeverageType;
import com.sbt.pprb.qa.test_task.model.response.BeverageResponseResource;
import com.sbt.pprb.qa.test_task.model.response.BeverageVolumeResponseResource;
import com.sbt.pprb.qa.test_task.service.BeverageService;
import io.qameta.allure.Epic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Epic("Integration-тесты контроллеров")
@DisplayName("Тесты контроллера апи BeverageController")
class BeverageControllerTest extends ControllerTestContext {

    @MockBean
    private BeverageService beverageService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void itShouldReturnBeverages() throws Exception {
        // Given
        List<BeverageResponseResource> volumes = Arrays.asList(
                new BeverageResponseResource(1L, 5.0, BeverageType.SLURM,
                        Arrays.asList(
                                new BeverageVolumeResponseResource(1L, 0.35, 35),
                                new BeverageVolumeResponseResource(2L, 0.5, 50)
                        )
                ),
                new BeverageResponseResource(2L, 5.0, BeverageType.EXPRESSO,
                        Arrays.asList(
                                new BeverageVolumeResponseResource(3L, 0.2, 60),
                                new BeverageVolumeResponseResource(4L, 0.4, 40)
                        )
                ),
                new BeverageResponseResource(3L, 5.0, BeverageType.NUKA_COLA,
                        Arrays.asList(
                                new BeverageVolumeResponseResource(5L, 0.35, 35),
                                new BeverageVolumeResponseResource(6L, 0.5, 50)
                        )
                )
        );
        when(beverageService.getVolumes()).thenReturn(volumes);

        // When
        MockHttpServletRequestBuilder request = get("/api/v1/beverages/volumes").with(auth);

        // Then
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", not(emptyArray())))
                .andExpect(jsonPath("$[0]", notNullValue()))
                .andExpect(jsonPath("$[0].id").value(volumes.get(0).getId()))
                .andExpect(jsonPath("$[0].availableVolume").value(volumes.get(0).getAvailableVolume()))
                .andExpect(jsonPath("$[0].beverageType").value(volumes.get(0).getBeverageType().getType()))
                .andExpect(jsonPath("$[0].beverageVolumes", not(emptyArray())))
                .andExpect(jsonPath("$[0].beverageVolumes[0].id").value(volumes.get(0).getBeverageVolumes().get(0).getId()))
                .andExpect(jsonPath("$[0].beverageVolumes[0].volume").value(volumes.get(0).getBeverageVolumes().get(0).getVolume()))
                .andExpect(jsonPath("$[0].beverageVolumes[0].price").value(volumes.get(0).getBeverageVolumes().get(0).getPrice()))
                .andExpect(jsonPath("$[0]", notNullValue()))
                .andExpect(jsonPath("$[0].id").value(volumes.get(0).getId()))
                .andExpect(jsonPath("$[0].availableVolume").value(volumes.get(0).getAvailableVolume()))
                .andExpect(jsonPath("$[0].beverageType").value(volumes.get(0).getBeverageType().getType()))
                .andExpect(jsonPath("$[0].beverageVolumes", not(emptyArray())))
                .andExpect(jsonPath("$[0].beverageVolumes[1].id").value(volumes.get(0).getBeverageVolumes().get(1).getId()))
                .andExpect(jsonPath("$[0].beverageVolumes[1].volume").value(volumes.get(0).getBeverageVolumes().get(1).getVolume()))
                .andExpect(jsonPath("$[0].beverageVolumes[1].price").value(volumes.get(0).getBeverageVolumes().get(1).getPrice()))
                .andExpect(jsonPath("$[1]", notNullValue()))
                .andExpect(jsonPath("$[1].id").value(volumes.get(1).getId()))
                .andExpect(jsonPath("$[1].availableVolume").value(volumes.get(1).getAvailableVolume()))
                .andExpect(jsonPath("$[1].beverageType").value(volumes.get(1).getBeverageType().getType()))
                .andExpect(jsonPath("$[1].beverageVolumes", not(emptyArray())))
                .andExpect(jsonPath("$[1].beverageVolumes[0].id").value(volumes.get(1).getBeverageVolumes().get(0).getId()))
                .andExpect(jsonPath("$[1].beverageVolumes[0].volume").value(volumes.get(1).getBeverageVolumes().get(0).getVolume()))
                .andExpect(jsonPath("$[1].beverageVolumes[0].price").value(volumes.get(1).getBeverageVolumes().get(0).getPrice()))
                .andExpect(jsonPath("$[1]", notNullValue()))
                .andExpect(jsonPath("$[1].id").value(volumes.get(1).getId()))
                .andExpect(jsonPath("$[1].availableVolume").value(volumes.get(1).getAvailableVolume()))
                .andExpect(jsonPath("$[1].beverageType").value(volumes.get(1).getBeverageType().getType()))
                .andExpect(jsonPath("$[1].beverageVolumes", not(emptyArray())))
                .andExpect(jsonPath("$[1].beverageVolumes[1].id").value(volumes.get(1).getBeverageVolumes().get(1).getId()))
                .andExpect(jsonPath("$[1].beverageVolumes[1].volume").value(volumes.get(1).getBeverageVolumes().get(1).getVolume()))
                .andExpect(jsonPath("$[1].beverageVolumes[1].price").value(volumes.get(1).getBeverageVolumes().get(1).getPrice()))
                .andExpect(jsonPath("$[2]", notNullValue()))
                .andExpect(jsonPath("$[2].id").value(volumes.get(2).getId()))
                .andExpect(jsonPath("$[2].availableVolume").value(volumes.get(2).getAvailableVolume()))
                .andExpect(jsonPath("$[2].beverageType").value(volumes.get(2).getBeverageType().getType()))
                .andExpect(jsonPath("$[2].beverageVolumes", not(emptyArray())))
                .andExpect(jsonPath("$[2].beverageVolumes[0].id").value(volumes.get(2).getBeverageVolumes().get(0).getId()))
                .andExpect(jsonPath("$[2].beverageVolumes[0].volume").value(volumes.get(2).getBeverageVolumes().get(0).getVolume()))
                .andExpect(jsonPath("$[2].beverageVolumes[0].price").value(volumes.get(2).getBeverageVolumes().get(0).getPrice()))
                .andExpect(jsonPath("$[2]", notNullValue()))
                .andExpect(jsonPath("$[2].id").value(volumes.get(2).getId()))
                .andExpect(jsonPath("$[2].availableVolume").value(volumes.get(2).getAvailableVolume()))
                .andExpect(jsonPath("$[2].beverageType").value(volumes.get(2).getBeverageType().getType()))
                .andExpect(jsonPath("$[2].beverageVolumes", not(emptyArray())))
                .andExpect(jsonPath("$[2].beverageVolumes[1].id").value(volumes.get(2).getBeverageVolumes().get(1).getId()))
                .andExpect(jsonPath("$[2].beverageVolumes[1].volume").value(volumes.get(2).getBeverageVolumes().get(1).getVolume()))
                .andExpect(jsonPath("$[2].beverageVolumes[1].price").value(volumes.get(2).getBeverageVolumes().get(1).getPrice()));
    }

    @Test
    void itShouldReturnUnauthorizedOnBeveragesRequest() throws Exception {
        // When
        MockHttpServletRequestBuilder request = get("/api/v1/beverages/volumes");

        // Then
        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    void itShouldAddAvailableVolume() throws Exception {
        // Given
        doNothing().when(beverageService).addVolume(anyLong(), anyDouble());

        // When
        MockHttpServletRequestBuilder request = put("/api/v1/beverages/1")
                .with(auth)
                .param("volume", "2.5");

        // Then
        mockMvc.perform(request)
                .andExpect(status().isOk());
        verify(beverageService).addVolume(anyLong(), anyDouble());
    }

    @Test
    void itShouldReturnUnauthorizedOnAddAvailableVolumeRequest() throws Exception {
        // When
        MockHttpServletRequestBuilder request = put("/api/v1/beverages/1")
                .param("volume", "2.5");

        // Then
        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }
}