package com.rygital.CaptchaService;

import com.rygital.CaptchaService.captcha.manager.CaptchaManager;
import com.rygital.CaptchaService.captcha.model.RequestIdAndAnswer;
import com.rygital.CaptchaService.captcha.model.Status;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CaptchaManagerTests {

    @Value("${captcha.manager.amountOfCaptchaTests:3}")
    private int amountOfCaptchaTests;

    @Autowired
    private CaptchaManager captchaManager;

    private static final String PUBLIC_KEY = "publickey";
    private static final String SECRET_KEY = "secretkey";

    @Test
    public void testValidAnswer() {
        CaptchaManager captchaManager = this.captchaManager;

        RequestIdAndAnswer requestIdAndAnswer = captchaManager.initNewCaptchaSession(PUBLIC_KEY);
        String base64image = captchaManager.getBase64CaptchaImage(PUBLIC_KEY, requestIdAndAnswer.getRequestId());
        String responseToken = captchaManager.solveCaptcha(
                PUBLIC_KEY,
                SECRET_KEY,
                requestIdAndAnswer.getRequestId(),
                requestIdAndAnswer.getAnswer()
        );

        Assert.assertEquals(Status.OK, captchaManager.verifyResult(SECRET_KEY, responseToken));
        Assert.assertEquals(null, captchaManager.verifyResult(SECRET_KEY, responseToken));
    }

    @Test
    public void testInvalidAnswer() {
        CaptchaManager captchaManager = this.captchaManager;

        RequestIdAndAnswer requestIdAndAnswer = captchaManager.initNewCaptchaSession(PUBLIC_KEY);
        String responseToken = captchaManager.solveCaptcha(
                PUBLIC_KEY,
                SECRET_KEY,
                requestIdAndAnswer.getRequestId(),
                "invalid_answer"
        );

        Assert.assertEquals(Status.NOT_MATCH, captchaManager.verifyResult(SECRET_KEY, responseToken));
        Assert.assertEquals(null, captchaManager.verifyResult(SECRET_KEY, responseToken));
    }

    @Test
    public void testMultipleCaptchaInitialisation() {
        CaptchaManager captchaManager = this.captchaManager;

        captchaManager.initNewCaptchaSession(PUBLIC_KEY);
        captchaManager.initNewCaptchaSession(PUBLIC_KEY);
        captchaManager.initNewCaptchaSession(PUBLIC_KEY);
        captchaManager.initNewCaptchaSession(PUBLIC_KEY);
        RequestIdAndAnswer requestIdAndAnswer = captchaManager.initNewCaptchaSession(PUBLIC_KEY);
        String responseToken = captchaManager.solveCaptcha(
                PUBLIC_KEY,
                SECRET_KEY,
                requestIdAndAnswer.getRequestId(),
                requestIdAndAnswer.getAnswer()
        );

        Assert.assertEquals(Status.OK, captchaManager.verifyResult(SECRET_KEY, responseToken));
        Assert.assertEquals(null, captchaManager.verifyResult(SECRET_KEY, responseToken));
    }

    @Test
    public void testMultipleAnswersWithError() {
        CaptchaManager captchaManager = this.captchaManager;

        for (int i = 0; i < amountOfCaptchaTests; i++) {
            RequestIdAndAnswer requestIdAndAnswer = captchaManager.initNewCaptchaSession(PUBLIC_KEY);
            String responseToken = captchaManager.solveCaptcha(
                    PUBLIC_KEY,
                    SECRET_KEY,
                    requestIdAndAnswer.getRequestId(),
                    "invalid_answer"
            );

            if (i < amountOfCaptchaTests - 1) Assert.assertEquals(Status.NOT_MATCH, captchaManager.verifyResult(SECRET_KEY, responseToken));
            else Assert.assertEquals(null, captchaManager.verifyResult(SECRET_KEY, responseToken));
        }
    }

    @Test
    public void testMultipleAnswers() {
        CaptchaManager captchaManager = this.captchaManager;

        for (int i = 0; i < amountOfCaptchaTests; i++) {
            RequestIdAndAnswer requestIdAndAnswer = captchaManager.initNewCaptchaSession(PUBLIC_KEY);

            if (i < amountOfCaptchaTests - 1) {
                String responseToken = captchaManager.solveCaptcha(
                        PUBLIC_KEY,
                        SECRET_KEY,
                        requestIdAndAnswer.getRequestId(),
                        "invalid_answer"
                );

                Assert.assertEquals(Status.NOT_MATCH, captchaManager.verifyResult(SECRET_KEY, responseToken));
            } else {
                String responseToken = captchaManager.solveCaptcha(
                        PUBLIC_KEY,
                        SECRET_KEY,
                        requestIdAndAnswer.getRequestId(),
                        requestIdAndAnswer.getAnswer()
                );

                Assert.assertEquals(Status.OK, captchaManager.verifyResult(SECRET_KEY, responseToken));
            }
        }
    }
}