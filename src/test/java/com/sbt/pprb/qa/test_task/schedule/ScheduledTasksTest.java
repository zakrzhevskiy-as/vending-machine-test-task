package com.sbt.pprb.qa.test_task.schedule;

import com.sbt.pprb.qa.test_task.config.ScheduledConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

@SpringBootTest
@SpringJUnitConfig(ScheduledConfig.class)
class ScheduledTasksTest {

    @SpyBean
    private ScheduledTasks underTest;

    @Test
    void scheduledSelfPing() {
        await()
                .atMost(Duration.ofSeconds(7L))
                .untilAsserted(() -> verify(underTest, atLeast(5)).scheduledSelfPing());
    }
}