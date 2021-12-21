package com.sbt.pprb.qa.test_task.controller.api;

import com.sbt.pprb.qa.test_task.model.dto.Beverage;
import com.sbt.pprb.qa.test_task.model.dto.BeverageVolume;
import com.sbt.pprb.qa.test_task.model.response.BeverageResponseResource;
import com.sbt.pprb.qa.test_task.service.BeverageService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/beverages")
public class BeverageController {

    private BeverageService service;

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Beverage>> getBeverages() {
        return ResponseEntity.ok(service.getBeverages());
    }

    @GetMapping(path = "{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Beverage> getBeverage(@PathVariable Long id) {
        return ResponseEntity.ok(service.getBeverage(id));
    }

    @GetMapping(path = "volumes", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<BeverageResponseResource>> getVolumes() {
        return ResponseEntity.ok(service.getVolumes());
    }

    @GetMapping(path = "volumes/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BeverageVolume> getVolume(@PathVariable Long id) {
        return ResponseEntity.ok(service.getVolume(id));
    }

    @GetMapping(path = "{id}/volumes", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<BeverageVolume>> getBeverageVolumes(@PathVariable Long id) {
        return ResponseEntity.ok(service.getBeverageVolumes(id));
    }
}
