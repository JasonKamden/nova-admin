package org.dromara.nova.system.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.nova.common.cache.util.RedisUtils;
import org.dromara.nova.common.core.enums.CommonResultCode;
import org.dromara.nova.common.core.exception.BusinessException;
import org.dromara.nova.system.constant.SecurityCacheConstants;
import org.dromara.nova.system.dto.response.CaptchaRespDto;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 使用 Java2D 生成轻量图形验证码，并将答案短期存入 Redis。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LoginCaptchaServiceImpl implements LoginCaptchaService {
    private static final String CAPTCHA_CHARS = "23456789ABCDEFGHJKMNPQRSTUVWXYZ";
    private static final int CAPTCHA_LENGTH = 4;
    private static final int IMAGE_WIDTH = 132;
    private static final int IMAGE_HEIGHT = 44;

    private final RedisUtils redisUtils;
    private final CaptchaRateGuard captchaRateGuard;

    /**
     * 生成新的图形验证码并写入 Redis。
     */
    @Override
    public CaptchaRespDto generate() {
        captchaRateGuard.check();

        String captchaId = UUID.randomUUID().toString();
        String answer = randomCode();
        String imageBase64 = render(answer);

        redisUtils.set(SecurityCacheConstants.CAPTCHA_KEY_PREFIX + captchaId, answer, SecurityCacheConstants.CAPTCHA_TTL);
        log.info("Captcha generated captchaId={}", captchaId);

        return new CaptchaRespDto(captchaId, imageBase64);
    }

    /**
     * 校验并一次性消费验证码。
     *
     * @param captchaId   验证码唯一标识
     * @param captchaCode 用户输入验证码
     */
    @Override
    public void validate(String captchaId, String captchaCode) {
        String key = SecurityCacheConstants.CAPTCHA_KEY_PREFIX + captchaId;
        String answer = redisUtils.getAndDelete(key);

        if (answer == null || answer.isBlank()) {
            log.warn("Captcha expired or missing captchaId={}", captchaId);
            throw new BusinessException(CommonResultCode.BAD_REQUEST, "验证码已失效，请重新获取");
        }

        if (!answer.equalsIgnoreCase(captchaCode == null ? "" : captchaCode.trim())) {
            log.warn("Captcha validation failed captchaId={}", captchaId);
            throw new BusinessException(CommonResultCode.BAD_REQUEST, "验证码错误");
        }
    }

    private String randomCode() {
        StringBuilder builder = new StringBuilder(CAPTCHA_LENGTH);
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int i = 0; i < CAPTCHA_LENGTH; i++) {
            builder.append(CAPTCHA_CHARS.charAt(random.nextInt(CAPTCHA_CHARS.length())));
        }

        return builder.toString();
    }

    private String render(String answer) {
        BufferedImage image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();

        try {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setColor(new Color(247, 248, 250));
            graphics.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);

            drawNoise(graphics);
            drawCode(graphics, answer);

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ImageIO.write(image, "png", output);
            String base64 = Base64.getEncoder().encodeToString(output.toByteArray());

            return "data:image/png;base64," + base64;
        } catch (IOException exception) {
            throw new BusinessException(CommonResultCode.INTERNAL_ERROR, "验证码生成失败");
        } finally {
            graphics.dispose();
        }
    }

    private void drawNoise(Graphics2D graphics) {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int i = 0; i < 6; i++) {
            graphics.setColor(new Color(random.nextInt(120, 190), random.nextInt(120, 190), random.nextInt(120, 190)));
            graphics.drawLine(random.nextInt(IMAGE_WIDTH), random.nextInt(IMAGE_HEIGHT), random.nextInt(IMAGE_WIDTH), random.nextInt(IMAGE_HEIGHT));
        }

        for (int i = 0; i < 25; i++) {
            graphics.setColor(new Color(random.nextInt(150, 210), random.nextInt(150, 210), random.nextInt(150, 210)));
            graphics.fillOval(random.nextInt(IMAGE_WIDTH), random.nextInt(IMAGE_HEIGHT), 2, 2);
        }
    }

    private void drawCode(Graphics2D graphics, String answer) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        graphics.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));

        for (int i = 0; i < answer.length(); i++) {
            graphics.setColor(new Color(random.nextInt(40, 110), random.nextInt(60, 130), random.nextInt(140, 220)));
            double angle = Math.toRadians(random.nextInt(-20, 21));
            int x = 18 + i * 26;
            int y = 30 + random.nextInt(0, 6);

            graphics.rotate(angle, x, y);
            graphics.drawString(String.valueOf(answer.charAt(i)), x, y);
            graphics.rotate(-angle, x, y);
        }
    }
}
