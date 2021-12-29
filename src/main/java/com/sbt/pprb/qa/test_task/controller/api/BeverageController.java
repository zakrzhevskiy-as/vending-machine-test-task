package com.sbt.pprb.qa.test_task.controller.api;

import com.sbt.pprb.qa.test_task.model.response.BeverageResponseResource;
import com.sbt.pprb.qa.test_task.service.BeverageService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/beverages")
public class BeverageController {

    private BeverageService service;

    @GetMapping(path = "volumes", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<BeverageResponseResource>> getVolumes() {
        return ResponseEntity.ok(service.getVolumes());
    }

    @PutMapping(path = "{id}")
    public void addAvailableVolume(@PathVariable Long id, @RequestParam Double volume) {
        service.addVolume(id, volume);
    }
}
