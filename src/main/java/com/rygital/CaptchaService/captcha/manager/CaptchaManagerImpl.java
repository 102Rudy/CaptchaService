package com.rygital.CaptchaService.captcha.manager;

import com.rygital.CaptchaService.captcha.generator.CaptchaGenerator;
import com.rygital.CaptchaService.captcha.model.CaptchaSession;
import com.rygital.CaptchaService.captcha.model.RequestIdAndAnswer;
import com.rygital.CaptchaService.captcha.model.Status;
import com.rygital.CaptchaService.captcha.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CaptchaManagerImpl implements CaptchaManager {

    @Value("${captcha.manager.length:6}")
    private int captchaTextLength;
    @Value("${captcha.manager.ttl:30}")
    private int ttl;
    @Value("${captcha.manager.amountOfCaptchaTests:3}")
    private int amountOfCaptchaTests;

    private static final int TOKEN_LENGTH = 8;

    private final CaptchaGenerator captchaGenerator;

    private ConcurrentHashMap<String, List<CaptchaSession>> captchaSessionsMap;
    private ConcurrentHashMap<String, Status> resultMap;

    @Autowired
    public CaptchaManagerImpl(CaptchaGenerator captchaGenerator) {
        this.captchaGenerator = captchaGenerator;

        captchaSessionsMap = new ConcurrentHashMap<>();
        resultMap = new ConcurrentHashMap<>();
    }

    @Override
    public RequestIdAndAnswer initNewCaptchaSession(String publicClientKey) {
        String randomString = Utils.generateRandomStr(captchaTextLength);

        List<CaptchaSession> sessions = captchaSessionsMap.get(publicClientKey);
        if (sessions == null) sessions = new ArrayList<>();
        sessions.add(new CaptchaSession(randomString, Calendar.getInstance().getTimeInMillis()));

        captchaSessionsMap.put(publicClientKey, sessions);

        return new RequestIdAndAnswer(sessions.size() - 1, randomString);
    }

    @Override
    public String getBase64CaptchaImage(String publicClientKey, int requestId) {
        String answer = captchaSessionsMap.get(publicClientKey).get(requestId).getAnswer();

        return Utils.encodeImageToBase64String(captchaGenerator.generateCaptchaImage(answer), "png");
    }

    @Override
    public String solveCaptcha(String publicClientKey, String secretKey, int requestId, String enteredAnswer) {
        try {
            List<CaptchaSession> captchaSessions = captchaSessionsMap.get(publicClientKey);

            Status captchaSolvingResult = tryToSolveCaptchaSession(captchaSessions, requestId, enteredAnswer);

            if (captchaSolvingResult == Status.BLOCK_SESSION) {
                captchaSessionsMap.remove(publicClientKey);
                trimToSizeCaptchaSessionsMap();

                return "403";
            }

            String responseToken = Utils.generateRandomStr(TOKEN_LENGTH);

            resultMap.put(secretKey + responseToken, captchaSolvingResult);
            return responseToken;
        } catch (NullPointerException e) {
            return "403";
        }
    }

    protected Status tryToSolveCaptchaSession(List<CaptchaSession> captchaSessions,
                                              int requestId, String enteredAnswer) {
        Status status = checkEnteredAnswer(captchaSessions.get(requestId), enteredAnswer);

        Long amountOfAnswered = captchaSessions.stream()
                .filter(CaptchaSession::isAnswered)
                .count();

        if (amountOfAnswered >= amountOfCaptchaTests && status != Status.OK) {
            return Status.BLOCK_SESSION;
        }

        return status;
    }

    protected Status checkEnteredAnswer(CaptchaSession captchaSession, String enteredAnswer) {
        if (captchaSession.isAnswered()) {
            return Status.ALREADY_ANSWERED;
        }

        captchaSession.setAnswered(true);

        if (Calendar.getInstance().getTimeInMillis() - captchaSession.getCreationTime() > ttl*1000) {
            return Status.EXPIRED;
        }

        if (!Objects.equals(captchaSession.getAnswer(), enteredAnswer)) {
            return Status.NOT_MATCH;
        }

        return Status.OK;
    }

    @Override
    public Status verifyResult(String secretKey, String responseToken) {
        Status status = resultMap.get(secretKey + responseToken);
        resultMap.remove(secretKey + responseToken);

        trimToSizeResultMap();

        return status;
    }

    protected void trimToSizeCaptchaSessionsMap() {
        if (captchaSessionsMap.size() > 32)
            captchaSessionsMap = new ConcurrentHashMap<>(captchaSessionsMap);
    }

    protected void trimToSizeResultMap() {
        if (resultMap.size() > 32)
            resultMap = new ConcurrentHashMap<>(resultMap);
    }
}