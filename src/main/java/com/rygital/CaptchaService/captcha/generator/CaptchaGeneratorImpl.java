package com.rygital.CaptchaService.captcha.generator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;

@Component
public class CaptchaGeneratorImpl implements CaptchaGenerator {

    @Value("${captcha.generator.image.width:180}")
    private int width;
    @Value("${captcha.generator.image.height:70}")
    private int height;

    @Value("${captcha.generator.image.rotationAmplitude:25}")
    private int rotationAmplitude;
    @Value("${captcha.generator.image.scaleAmplitude:15}")
    private int scaleAmplitude;
    @Value("${captcha.generator.image.shearAmplitude:10}")
    private int shearAmplitude;

    @Override
    public BufferedImage generateCaptchaImage(String captchaText) {
        if (captchaText == null || captchaText.isEmpty()) {
            throw new IllegalStateException("Captcha text is not set");
        }

        int charWidth = width / captchaText.length();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setBackground(Color.WHITE);
        g2d.setColor(Color.BLACK);

        g2d.clearRect(0, 0, width, height);

        int x = 0;
        for (char word : captchaText.toCharArray()) {
            drawCharacter(g2d, word, x, charWidth);
            x += charWidth;
        }

        g2d.dispose();

        return image;
    }

    protected void drawCharacter(Graphics2D g2d, char ch, int x, int boxWidth) {

        double shearX = (Math.random() - 1) * shearAmplitude / 100;
        double shearY = (Math.random() - 1) * shearAmplitude / 100;
        double degree = (Math.random() * rotationAmplitude * 2) - rotationAmplitude;
        double scaleX = 1 - (Math.random() * scaleAmplitude / 100);
        double scaleY = 1 - (Math.random() * scaleAmplitude / 100);

        Graphics2D cg2d = (Graphics2D) g2d.create();
        Font font = new Font(Font.MONOSPACED, Font.PLAIN, 40);
        font.deriveFont(30f);

        cg2d.setFont(font);

        cg2d.shear(shearX, shearY);
        cg2d.translate(x + (boxWidth / 2), height / 2);
        cg2d.rotate(degree * Math.PI / 90);
        cg2d.scale(scaleX, scaleY);

        FontMetrics fm = cg2d.getFontMetrics();
        int charWidth = fm.charWidth(ch);
        int charHeight = fm.getAscent() + fm.getDescent();

        cg2d.drawString(String.valueOf(ch), -(charWidth / 2), fm.getAscent() - (charHeight / 2));

        cg2d.dispose();
    }
}