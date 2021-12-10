package main.service;
import com.github.cage.Cage;
import com.github.cage.YCage;
import main.api.response.CaptchaResponse;
import main.model.CaptchaCode;
import main.model.repositories.CaptchaCodeRepository;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Random;

@Service
public class CaptchaService {

    @Autowired
    private CaptchaCodeRepository captchaCodeRepository;

    final private String TITLE = "data:image/png;base64, ";

    public CaptchaResponse generateAndGetCaptcha() throws Exception {
        CaptchaResponse captchaResponse = new CaptchaResponse();
        char[] availableChars = "abcdefghijklmnopqrstuvwxyz0123456789"
                .toCharArray();
        StringBuilder randomSecretCode = new StringBuilder();
        StringBuilder randomCode = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            randomCode.append(availableChars[random.nextInt(availableChars
                    .length)]);
            randomSecretCode.append(availableChars[random.nextInt(availableChars
                    .length)]);
        }
        captchaResponse.setSecret(randomSecretCode.toString());
        Cage cage = new YCage();
        BufferedImage bf = cage.drawImage(randomCode.toString());
        BufferedImage scaledImage = Scalr.
                resize(bf, 100, 35, Scalr.OP_GRAYSCALE);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(scaledImage, "png", baos);
        byte[] fileContent = baos.toByteArray();
        String encodedString = Base64.getEncoder().encodeToString(fileContent);
        captchaResponse.setImage(TITLE + encodedString);
        CaptchaCode captchaCode = new CaptchaCode();
        captchaCode.setTime(LocalDateTime.now());
        captchaCode.setCode(randomCode.toString());
        captchaCode.setSecretCode(randomSecretCode.toString());
        captchaCodeRepository.save(captchaCode);

        return captchaResponse;
    }

    public void deleteOldCaptchasFromRepository() {
        captchaCodeRepository.deleteByTimeIsAfter(LocalDateTime
                .now().plusDays(-1));
    }

}
