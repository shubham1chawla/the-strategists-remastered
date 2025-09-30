package com.strategists.game.service.impl;

import com.strategists.game.configuration.properties.ExternalAPIEndpointConfigurationProperties;
import com.strategists.game.configuration.properties.PermissionsConfigurationProperties;
import com.strategists.game.response.GoogleRecaptchaVerificationResponse;
import com.strategists.game.response.PermissionGroupResponse;
import com.strategists.game.service.PermissionsService;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Log4j2
@Service
public class PermissionsServiceImpl extends AbstractExternalService implements PermissionsService {

    @Autowired
    private PermissionsConfigurationProperties properties;

    @Override
    public boolean verifyGoogleRecaptcha(String clientToken) {
        log.info("Verifying Google Recaptcha...");

        // Checking if API call is by-passed!
        if (properties.googleRecaptcha().bypassForTesting()) {
            log.warn("Bypassing Google Recaptcha verification for testing! This should only happen for local testing!");
            return true;
        }

        // Verifying Google Recaptcha
        try {
            final var body = Map.of("client_token", clientToken);
            final var restTemplate = new RestTemplate();
            final var entity = restTemplate.postForEntity(properties.googleRecaptcha().apiEndpoint(), body, GoogleRecaptchaVerificationResponse.class);
            if (entity.getStatusCode().is2xxSuccessful() && Objects.nonNull(entity.getBody())) {
                return entity.getBody().isSuccess();
            } else {
                throw new RuntimeException("Something went wrong calling Google Recaptcha API!");
            }
        } catch (Exception ex) {
            log.error("Unable to verify Google Recaptcha! Message: {}", ex.getMessage());
            return false;
        }
    }

    @Override
    public Optional<PermissionGroupResponse> getPermissionGroupByEmail(String email) {
        log.info("Loading permission group...");

        // Checking if API call is by-passed!
        if (properties.permissionGroup().bypassForTesting()) {
            log.warn("Bypassing permission group for testing! This should only happen for local testing!");
            return Optional.of(PermissionGroupResponse.allowAll(email));
        }

        // Fetching permission group
        try {
            final var body = Map.of("email", email);
            final var restTemplate = new RestTemplate();
            final var entity = restTemplate.postForEntity(properties.permissionGroup().apiEndpoint(), body, PermissionGroupResponse.class);
            if (entity.getStatusCode().is2xxSuccessful()) {
                return Optional.ofNullable(entity.getBody());
            } else if (entity.getStatusCode().is4xxClientError()) {
                throw new RuntimeException("No permission group found!");
            } else {
                throw new RuntimeException("Something went wrong calling permission group API!");
            }
        } catch (Exception ex) {
            log.error("Unable to load permission group! Message: {}", ex.getMessage());
            return Optional.empty();
        }
    }

    @Override
    protected ExternalAPIEndpointConfigurationProperties getHealthCheck() {
        return properties.healthCheck();
    }

    @Override
    protected String getExternalServiceName() {
        return "strategists-permissions";
    }

    @Override
    protected Logger getLogger() {
        return log;
    }
}
