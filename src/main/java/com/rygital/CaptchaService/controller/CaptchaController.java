package com.rygital.CaptchaService.controller;

import com.rygital.CaptchaService.captcha.manager.CaptchaManager;
import com.rygital.CaptchaService.captcha.model.RequestIdAndAnswer;
import com.rygital.CaptchaService.captcha.model.Status;
import com.rygital.CaptchaService.model.Result;
import com.rygital.CaptchaService.model.Solution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/captcha")
public class CaptchaController {

    @Value("${captcha.production:true}")
    private Boolean production;

    private final ClientController clientController;
    private final CaptchaManager captchaManager;

    @Autowired
    public CaptchaController(ClientController clientController, CaptchaManager captchaManager) {
        this.clientController = clientController;
        this.captchaManager = captchaManager;
    }

    @GetMapping("/new")
    public ResponseEntity newCaptcha(@RequestParam("publicKey") String publicKey) {
        if (clientController.getSecretKeyOfUserSession(publicKey) == null)
            return responseForbidden();

        RequestIdAndAnswer requestIdAndAnswer = captchaManager.initNewCaptchaSession(publicKey);

        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("request", String.valueOf(requestIdAndAnswer.getRequestId()));
        if (!production) responseMap.put("answer", requestIdAndAnswer.getAnswer());

        return ResponseEntity.ok().body(responseMap);
    }

    @GetMapping("/image")
    public ResponseEntity getBase64Image(@RequestParam("publicKey") String publicKey,
                                         @RequestParam("request") int request) {
        try {
            Map<String, String> responseMap = new HashMap<>();
            responseMap.put("base64image", captchaManager.getBase64CaptchaImage(publicKey, request));
            return ResponseEntity.ok().body(responseMap);
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/solve")
    public ResponseEntity solveCaptcha(@RequestBody Solution solution) {
        String secretKey = clientController.getSecretKeyOfUserSession(solution.getPublicKey());
        if (secretKey == null) return responseForbidden();

        String responseToken = captchaManager
                .solveCaptcha(solution.getPublicKey(), secretKey, solution.getRequest(), solution.getEnteredAnswer());

        if (responseToken.equals("403")) {
            clientController.removeUserSession(solution.getPublicKey());
            return responseForbidden();
        }

        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("response", responseToken);

        return ResponseEntity.ok().body(responseMap);
    }

    private ResponseEntity responseForbidden() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @GetMapping("/verify")
    public ResponseEntity verify(@RequestParam("secretKey") String secretKey,
                                 @RequestParam("response") String response) {
        Status status = captchaManager.verifyResult(secretKey, response);
        if (status == null || status == Status.BLOCK_SESSION)
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new Result(false, "current session is blocked"));

        switch (status) {
            case ALREADY_ANSWERED:
                return ResponseEntity.ok()
                        .body(new Result(false, "already answered"));
            case EXPIRED:
                return ResponseEntity.ok()
                        .body(new Result(false, "expired"));
            case NOT_MATCH:
                return ResponseEntity.ok()
                        .body(new Result(false, "not match"));
            case OK:
            default:
                return ResponseEntity.ok().body(new Result(true, null));
        }
    }
}