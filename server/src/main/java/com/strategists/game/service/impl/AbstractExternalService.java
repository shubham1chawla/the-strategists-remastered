package com.strategists.game.service.impl;

import com.strategists.game.configuration.properties.ExternalAPIEndpointConfigurationProperties;
import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.Logger;
import org.springframework.web.client.RestTemplate;

import java.net.ConnectException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

public abstract class AbstractExternalService {

    private static final int MAX_RETRIES = 10;
    private static final Duration TIMEOUT = Duration.of(10, ChronoUnit.SECONDS);

    @PostConstruct
    protected void waitUntilReady() throws ConnectException, InterruptedException {
        final var healthCheckApi = getHealthCheckApi();
        final var externalServiceName = getExternalServiceName();
        final var log = getLogger();

        // Checking if health check is enabled
        if (!healthCheckApi.enabled()) {
            log.warn("Skipping Health Check API call! Assuming External Service '{}' is ready to use.", externalServiceName);
            return;
        }

        // Checking if external service is available
        log.info("Checking External Service '{}' readiness...", externalServiceName);
        final var restTemplate = new RestTemplate();
        var remainingTries = MAX_RETRIES;
        while (remainingTries > 0) {
            try {
                final var response = restTemplate.getForEntity(healthCheckApi.endpoint(), String.class);
                if (!response.getStatusCode().is2xxSuccessful()) {
                    throw new ConnectException("Received unsuccessful (!=2xx) status code!");
                }
                break;
            } catch (Exception ex) {
                remainingTries--;
                log.warn(" External Service '{}' not ready! {} retries remaining...", externalServiceName, remainingTries);
            }

            // Sleeping until timeout
            Thread.sleep(TIMEOUT);
        }
        if (remainingTries == 0) {
            final var msg = String.format("External Service '%s' not ready after %s retries! Endpoint: %s", externalServiceName, MAX_RETRIES, healthCheckApi.endpoint());
            log.error(msg);
            throw new ConnectException(msg);
        }
        log.info(" External Service '{}' ready to use!", externalServiceName);
    }

    protected abstract String getExternalServiceName();

    protected abstract ExternalAPIEndpointConfigurationProperties getHealthCheckApi();

    protected abstract Logger getLogger();

}
