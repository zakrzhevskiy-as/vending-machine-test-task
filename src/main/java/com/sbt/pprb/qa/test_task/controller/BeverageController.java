package com.sbt.pprb.qa.test_task.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.sbt.pprb.qa.test_task.model.Beverage;
import com.sbt.pprb.qa.test_task.model.BeverageVolume;
import com.sbt.pprb.qa.test_task.model.Views;
import com.sbt.pprb.qa.test_task.service.BeverageService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/beverages")
public class BeverageController {

    private BeverageService service;

    @JsonView(Views.Beverage.class)
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Beverage>> getBeverages() {
        return ResponseEntity.ok(service.getBeverages());
    }

    @JsonView(Views.Beverage.class)
    @GetMapping(path = "{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Beverage> getBeverage(@PathVariable Long id) {
        return ResponseEntity.ok(service.getBeverage(id));
    }

    @JsonView(Views.Beverage.class)
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Beverage> createBeverage(Beverage beverage) throws URISyntaxException {
        Beverage created = service.createBeverage(beverage);
        return ResponseEntity.created(new URI("/api/beverages/" + created.getId())).body(created);
    }

    @JsonView(Views.Beverage.class)
    @PatchMapping(path = "{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Beverage> updateBeverage(@PathVariable Long id, Beverage beverage) {
        return ResponseEntity.ok(service.updateBeverage(id, beverage));
    }

    @JsonView(Views.BeverageVolume.class)
    @GetMapping(path = "volumes", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<BeverageVolume>> getVolumes() {
        return ResponseEntity.ok(service.getVolumes());
    }

    @JsonView(Views.BeverageVolume.class)
    @GetMapping(path = "volumes/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BeverageVolume> getVolume(@PathVariable Long id) {
        return ResponseEntity.ok(service.getVolume(id));
    }

    @JsonView(Views.BeverageVolume.class)
    @GetMapping(path = "{id}/volumes", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<BeverageVolume>> getBeverageVolumes(@PathVariable Long id) {
        return ResponseEntity.ok(service.getBeverageVolumes(id));
    }

    @PostMapping(path = "{id}/volumes", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BeverageVolume> createVolume(@PathVariable Long id, BeverageVolume beverageVolume) throws URISyntaxException {
        BeverageVolume created = service.createVolume(id, beverageVolume);
        return ResponseEntity.created(new URI("/api/beverages/volumes/" + created.getId())).body(created);
    }

    @PatchMapping(path = "volumes/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BeverageVolume> updateVolume(@PathVariable Long id, BeverageVolume beverageVolume) {
        return ResponseEntity.ok(service.updateVolume(id, beverageVolume));
    }
}
