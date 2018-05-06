package com.rygital.CaptchaService.captcha.model;

public class CaptchaSession {
    private final String answer;
    private final Long creationTime;
    private boolean answered;

    public CaptchaSession(String answer, Long creationTime) {
        this.answer = answer;
        this.creationTime = creationTime;
        answered = false;
    }

    public String getAnswer() {
        return answer;
    }

    public Long getCreationTime() {
        return creationTime;
    }

    public boolean isAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }
}
