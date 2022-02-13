package com.sbt.pprb.qa.test_task.service;

import com.sbt.pprb.qa.test_task.model.dto.AppUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ApplicationInfoService {

    @Value("${SPRING_DB_URL:jdbc:postgresql://localhost:5432/test-task}")
    private String jdbcUrl;
    @Value("${system.db.schema}")
    private String schema;
    @Value("${system.db.reader.user}")
    private String username;
    @Value("${system.db.reader.password}")
    private String password;
    @Value("${system.rest.auth.type}")
    private String authType;

    public Map<String, String> getDbConfig() {
        Map<String, String> body = new HashMap<>();
        body.put("url", jdbcUrl);
        body.put("schema", schema);
        body.put("username", username);
        body.put("password", password);

        return body;
    }

    public Map<String, String> getRestConfig() {
        Map<String, String> body = new HashMap<>();
        body.put("auth_type", authType);
        body.put("credentials", "Same as for UI");
        body.put("documentation", "swagger-ui/index.html");

        return body;
    }

    public Map<String, String> getAppInfo() {
        Map<String, String> appInfo = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            appInfo.put("username", ((AppUser) authentication.getPrincipal()).getUsername());
        }
        return appInfo;
    }
}
