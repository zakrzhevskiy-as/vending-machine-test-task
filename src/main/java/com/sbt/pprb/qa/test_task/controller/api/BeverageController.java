package com.sbt.pprb.qa.test_task.controller.api;

import com.sbt.pprb.qa.test_task.model.response.BeverageResponseResource;
import com.sbt.pprb.qa.test_task.service.BeverageService;
import io.swagger.annotations.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.sbt.pprb.qa.test_task.controller.EndpointPaths.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@AllArgsConstructor
@RequestMapping(path = BEVERAGES)
public class BeverageController {

    private final BeverageService service;

    @GetMapping(path = BEVERAGES_VOLUMES, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(code = 401, message = "Unauthorized")
    public ResponseEntity<List<BeverageResponseResource>> getVolumes() {
        return ResponseEntity.ok(service.getVolumes());
    }

    @PutMapping(path = BEVERAGES_ID)
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(code = 401, message = "Unauthorized")
    public void addAvailableVolume(@PathVariable Long id, @RequestParam Double volume) {
        service.addVolume(id, volume);
    }
}
