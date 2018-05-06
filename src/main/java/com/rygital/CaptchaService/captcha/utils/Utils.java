package com.rygital.CaptchaService.captcha.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class Utils {
    private static final char[] usedChars = {
            '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'a', 'B', 'b', 'C', 'c', 'D', 'd', 'E', 'e',
            'F', 'f', 'G', 'g', 'H', 'h', 'I', 'i', 'J', 'j',
            'K', 'k', 'L', 'l', 'M', 'm', 'N', 'n', 'O', 'o',
            'P', 'p', 'Q', 'q', 'R', 'r', 'S', 's', 'T', 't',
            'U', 'u', 'V', 'v', 'U', 'u', 'W', 'w', 'X', 'x',
            'Y', 'y', 'Z', 'z' };

    public static String generateRandomStr(int captchaTextLength) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < captchaTextLength; i++) {
            int randomIndex = (int) (Math.random() * (usedChars.length - 1));
            sb.append(usedChars[randomIndex]);
        }

        return String.valueOf(sb);
    }

    public static String encodeImageToBase64String(BufferedImage image, String type) {
        String imageString = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try {
            ImageIO.write(image, type, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            imageString = "data:image/"+type+";base64,"+ Base64.getEncoder().encodeToString(imageBytes);

            byteArrayOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageString;
    }
}