package com.rygital.CaptchaService.captcha.manager;

import com.rygital.CaptchaService.captcha.model.RequestIdAndAnswer;
import com.rygital.CaptchaService.captcha.model.Status;

public interface CaptchaManager {
    RequestIdAndAnswer initNewCaptchaSession(String publicClientKey);
    String getBase64CaptchaImage(String publicClientKey, int requestId);
    String solveCaptcha(String publicClientKey, String secretKey, int requestId, String enteredAnswer);
    Status verifyResult(String secretKey, String responseToken);
}
