package com.sbt.pprb.qa.test_task.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTasks {

    private final ServletContext servletContext;

    @Value("${system.rest.auth.username}")
    private String username;
    @Value("${system.rest.auth.password}")
    private String password;

    @Scheduled(cron = "${PING_CRON}")
    public void scheduledSelfPing() {
        log.debug("Pinging app");
        try {
            String urlString = "http://localhost:8080%s/api/v1/app-info".formatted(servletContext.getContextPath());
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(urlString))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NEVER)
                    .authenticator(new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password.toCharArray());
                        }
                    })
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            log.error("Failed to send request", e);
        } catch (URISyntaxException e) {
            log.error("Failed to create URI", e);
        }
    }
}
