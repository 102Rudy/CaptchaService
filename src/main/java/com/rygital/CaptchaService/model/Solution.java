package com.rygital.CaptchaService.model;

public class Solution {
    private String publicKey;
    private int request;
    private String enteredAnswer;

    public Solution() {}

    public Solution(String publicKey, int request, String enteredAnswer) {
        this.publicKey = publicKey;
        this.request = request;
        this.enteredAnswer = enteredAnswer;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public int getRequest() {
        return request;
    }

    public void setRequest(int request) {
        this.request = request;
    }

    public String getEnteredAnswer() {
        return enteredAnswer;
    }

    public void setEnteredAnswer(String enteredAnswer) {
        this.enteredAnswer = enteredAnswer;
    }
}
