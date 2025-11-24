package com.strategists.game.configuration.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import lombok.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "strategists.permissions")
public record PermissionsConfigurationProperties(boolean enabled,
                                                 @DefaultValue @Valid ExternalAPIEndpointConfigurationProperties healthCheckApi,
                                                 @DefaultValue @Valid ExternalAPIEndpointConfigurationProperties googleRecaptchaVerifyApi,
                                                 @DefaultValue @Valid ExternalAPIEndpointConfigurationProperties permissionGroupApi) {

    @AssertTrue(message = "Permissions enabled but health check API is disabled! Enable permissions' health check API!")
    boolean isHealthCheckAPIValid() {
        return !enabled || healthCheckApi.enabled();
    }

    @AssertTrue(message = "Permissions' Google ReCAPTCHA API is enabled but Permissions are disabled! Enable Permissions or disable Google ReCAPTCHA API!")
    boolean isGoogleRecaptchaAPIValid() {
        return !googleRecaptchaVerifyApi.enabled() || enabled;
    }

    @AssertTrue(message = "Permissions' permission group API is enabled but Permissions are disabled! Enable Permissions or disable permission group API!")
    boolean isPermissionGroupAPIValid() {
        return !permissionGroupApi.enabled() || enabled;
    }

    @NonNull
    @Override
    public String toString() {
        return "\n--------------------------------------------------" +
                "\nPermissions:" +
                "\n> Enabled: " + enabled +
                "\n> Health Check API: " +
                "\n\t> Enabled: " + healthCheckApi.enabled() +
                "\n\t> Endpoint: " + healthCheckApi.endpoint() +
                "\n> Google Recaptcha API: " +
                "\n\t> Enabled: " + googleRecaptchaVerifyApi.enabled() +
                "\n\t> Endpoint: " + googleRecaptchaVerifyApi.endpoint() +
                "\n> Permission Group API: " +
                "\n\t> Enabled: " + permissionGroupApi.enabled() +
                "\n\t> Endpoint: " + permissionGroupApi.endpoint() +
                "\n--------------------------------------------------";
    }

}
