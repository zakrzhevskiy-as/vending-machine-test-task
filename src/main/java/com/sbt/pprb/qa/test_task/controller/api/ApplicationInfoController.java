package com.sbt.pprb.qa.test_task.controller.api;

import com.sbt.pprb.qa.test_task.service.ApplicationInfoService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@AllArgsConstructor
@RequestMapping(path = "api/app-info")
public class ApplicationInfoController {

    private ApplicationInfoService service;

    @GetMapping(path = "database", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> dbConfig() {
        return ResponseEntity.ok(service.getDbConfig());
    }

    @GetMapping(path = "rest", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> restConfig() {
        return ResponseEntity.ok(service.getRestConfig());
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> appInfo() {
        return ResponseEntity.ok(service.getAppInfo());
    }
}
