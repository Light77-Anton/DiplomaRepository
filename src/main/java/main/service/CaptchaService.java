package main.service;
import com.github.cage.Cage;
import com.github.cage.YCage;
import main.api.response.CaptchaResponse;
import main.model.CaptchaCode;
import main.model.CaptchaCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Base64;
import java.util.Date;
import java.util.Random;

@Service
public class CaptchaService {

    @Autowired
    private CaptchaCodeRepository captchaCodeRepository;

    final private long CAPTCHA_TIME_EXISTENCE = 3600000;
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
        byte[] fileContent = cage.draw(randomCode.toString());
        String encodedString = Base64.getEncoder().encodeToString(fileContent);
        captchaResponse.setImage(TITLE + encodedString);
        /**
         * encodedString в base64 - это то,что нужно передавать в image + заголовок.Как понял.
         */
        CaptchaCode captchaCode = new CaptchaCode();
        captchaCode.setTime(new Date());
        captchaCode.setCode(randomCode.toString());
        captchaCode.setSecretCode(randomSecretCode.toString());
        captchaCodeRepository.save(captchaCode);

        return captchaResponse;
    }

    public void deleteOldCaptchasFromRepository() {
       Iterable<CaptchaCode> captchas = captchaCodeRepository.findAll();
       for (CaptchaCode captchaCode : captchas) {
           if (System.currentTimeMillis() > captchaCode.getTime().getTime()
                   + CAPTCHA_TIME_EXISTENCE) {
               captchaCodeRepository.delete(captchaCode);
           }
       }
    }

}
