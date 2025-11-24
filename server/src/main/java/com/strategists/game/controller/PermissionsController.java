package com.strategists.game.controller;

import com.strategists.game.request.GoogleRecaptchaVerificationRequest;
import com.strategists.game.service.PermissionsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/api")
@ConditionalOnProperty(name = "strategists.permissions.enabled", havingValue = "true")
public class PermissionsController {

    @Autowired
    private PermissionsService permissionsService;

    @PostMapping("/google-recaptcha-verify")
    public ResponseEntity<Void> verify(@RequestBody GoogleRecaptchaVerificationRequest request) {
        try {
            Assert.hasText(request.getClientToken(), "Client Token can't be empty");
            Assert.isTrue(permissionsService.verifyGoogleRecaptcha(request.getClientToken()), "User is not verified!");
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            log.warn("Recaptcha verification failed! Message: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

}
