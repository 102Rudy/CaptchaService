package com.rygital.CaptchaService.captcha.model;

public class RequestIdAndAnswer {
    private int requestId;
    private String answer;

    public RequestIdAndAnswer(int requestId, String answer) {
        this.requestId = requestId;
        this.answer = answer;
    }

    public int getRequestId() {
        return requestId;
    }

    public String getAnswer() {
        return answer;
    }
}
