package com.sbt.pprb.qa.test_task.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static com.sbt.pprb.qa.test_task.controller.EndpointPaths.APP_INFO;
import static com.sbt.pprb.qa.test_task.controller.EndpointPaths.APP_INFO_PING;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTasks {

    private final ServletContext servletContext;

    @Value("${system.rest.auth.username}")
    private String username;
    @Value("${system.rest.auth.password}")
    private String password;

    @Scheduled(cron = "${PING_CRON:0 0/5 * * * *}")
    public void scheduledSelfPing() {
        log.debug("Pinging app");
        try {
            Object[] parts = new String[]{servletContext.getContextPath(), APP_INFO, APP_INFO_PING};
            String urlString = "http://localhost:8080%s%s%s".formatted(parts);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(urlString))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NEVER)
                    .build()
                    .send(request, HttpResponse.BodyHandlers.discarding());
        } catch (IOException | InterruptedException e) {
            log.error("Failed to send request", e);
        } catch (URISyntaxException e) {
            log.error("Failed to create URI", e);
        }
    }
}
