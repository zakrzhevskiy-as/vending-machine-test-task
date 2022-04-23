package com.sbt.pprb.qa.test_task.controller.api;

import com.sbt.pprb.qa.test_task.service.ApplicationInfoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Collections;

import static com.sbt.pprb.qa.test_task.controller.EndpointPaths.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@ApiIgnore
@RestController
@AllArgsConstructor
@RequestMapping(path = APP_INFO)
public class ApplicationInfoController {

    private final ApplicationInfoService service;

    @GetMapping(path = APP_INFO_DATABASE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> dbConfig() {
        return ResponseEntity.ok(service.getDbConfig());
    }

    @GetMapping(path = APP_INFO_REST, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> restConfig() {
        return ResponseEntity.ok(service.getRestConfig());
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> appInfo() {
        return ResponseEntity.ok(service.getAppInfo());
    }

    @GetMapping(path = APP_INFO_PING, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> ping() {
        return ResponseEntity.ok(Collections.singletonMap("status", "active"));
    }
}
