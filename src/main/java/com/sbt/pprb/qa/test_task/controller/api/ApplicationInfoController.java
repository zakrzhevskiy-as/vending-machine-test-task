package com.sbt.pprb.qa.test_task.controller.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "api/app-info")
public class ApplicationInfoController {

    @Value("${spring.datasource.url}")
    private String jdbcUrl;
    @Value("${system.db.reader.user}")
    private String username;
    @Value("${system.db.reader.password}")
    private String password;
    @Value("${system.rest.auth.type}")
    private String authType;

    @GetMapping(path = "database", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> dbConfig() {
        Map<String, String> body = new HashMap<>();
        body.put("url", jdbcUrl);
        body.put("username", username);
        body.put("password", password);

        return ResponseEntity.ok(body);
    }

    @GetMapping(path = "rest", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> restConfig() {
        Map<String, String> body = new HashMap<>();
        body.put("auth_type", authType);
        body.put("credentials", "Same as for UI");
        body.put("documentation", "swagger-ui/index.html");

        return ResponseEntity.ok(body);
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> appInfo() {
        return ResponseEntity.ok().build();
    }
}
