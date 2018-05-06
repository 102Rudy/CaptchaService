package com.rygital.CaptchaService.captcha.generator;

import java.awt.image.BufferedImage;

public interface CaptchaGenerator {
    BufferedImage generateCaptchaImage(String captchaText);
}