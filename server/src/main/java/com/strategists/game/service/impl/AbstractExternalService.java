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
        final var healthCheck = getHealthCheck();
        final var externalServiceName = getExternalServiceName();
        final var log = getLogger();

        // Checking if health check is by-passed!
        if (healthCheck.bypassForTesting()) {
            log.warn("'{}' health-check bypassed! This should only happen for local testing!", externalServiceName);
            return;
        }

        // Checking if external service is available
        log.info("Checking '{}' readiness...", externalServiceName);
        final var restTemplate = new RestTemplate();
        var remainingTries = MAX_RETRIES;
        while (remainingTries > 0) {
            try {
                final var response = restTemplate.getForEntity(healthCheck.apiEndpoint(), String.class);
                if (!response.getStatusCode().is2xxSuccessful()) {
                    throw new ConnectException("Received unsuccessful (!=2xx) status code!");
                }
                break;
            } catch (Exception ex) {
                remainingTries--;
                log.warn("'{}' not ready! {} retries remaining...", externalServiceName, remainingTries);
            }

            // Sleeping until timeout
            Thread.sleep(TIMEOUT);
        }
        if (remainingTries == 0) {
            final var msg = String.format("'%s' not ready after %s retries! Endpoint: %s", externalServiceName, MAX_RETRIES, healthCheck.apiEndpoint());
            log.error(msg);
            throw new ConnectException(msg);
        }
        log.info("'{}' ready to use!", externalServiceName);
    }

    protected abstract String getExternalServiceName();

    protected abstract ExternalAPIEndpointConfigurationProperties getHealthCheck();

    protected abstract Logger getLogger();

}
