package com.strategists.game.configuration;

import com.strategists.game.service.UpdateService;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.TimeUnit;

@Log4j2
@Configuration
@ConditionalOnProperty(name = "strategists.sse-ping.enabled", havingValue = "true")
public class SSEPingConfiguration {

    @Autowired
    private UpdateService updateService;

    @PostConstruct
    public void setup() {
        log.info("SSE ping enabled.");
    }

    @Scheduled(fixedRate = 30, timeUnit = TimeUnit.SECONDS)
    public void ping() {
        log.debug("Sending SSE ping...");
        updateService.sendPing();
    }

}
