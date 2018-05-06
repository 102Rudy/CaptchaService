package com.rygital.CaptchaService.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/client")
public class ClientController {
    private ConcurrentHashMap<String, String> userSessionsMap;

    public ClientController() {
        userSessionsMap = new ConcurrentHashMap<>();
    }

    @PostMapping("/register")
    public Map<String, String> register() {
        String secretKey = UUID.randomUUID().toString();
        String publicKey = UUID.randomUUID().toString();

        userSessionsMap.put(publicKey, secretKey);
        Map<String, String> response = new HashMap<>();
        response.put("secretKey", secretKey);
        response.put("publicKey", publicKey);
        return response;
    }

    public String getSecretKeyOfUserSession(String publicKey) {
        return userSessionsMap.get(publicKey);
    }

    public void removeUserSession(String publicKey) {
        userSessionsMap.remove(publicKey);
        trimToSizeUserSessionsMap();
    }

    private void trimToSizeUserSessionsMap() {
        if (userSessionsMap.size() > 32) {
            userSessionsMap = new ConcurrentHashMap<>(userSessionsMap);
        }
    }
}
