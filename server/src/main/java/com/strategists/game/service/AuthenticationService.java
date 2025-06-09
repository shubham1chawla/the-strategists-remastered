package com.strategists.game.service;

import com.strategists.game.request.GoogleRecaptchaVerificationRequest;

public interface AuthenticationService {

    boolean verify(GoogleRecaptchaVerificationRequest request);

}
