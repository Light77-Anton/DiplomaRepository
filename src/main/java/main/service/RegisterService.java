package main.service;
import main.model.CaptchaCode;
import main.model.repositories.CaptchaCodeRepository;
import main.model.User;
import main.model.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class RegisterService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CaptchaCodeRepository captchaCodeRepository;

    public RegisterService() {

    }

    public String checkData(String email, String name,
                            String password, String captcha,
                            String captchaSecret) {

        if (email == null || name == null
                || password == null || captcha == null) {
            return "запрос не принят,нужно обязательно ввести"
                    + " эмэйл,имя,пароль и надпись с картинки для регистрации";
        }
        if (!email.contains("@") || email.equals("")) {
            return "это явно не эмэйл";
        }
        if (name.equals("")) {
            return "Имя указано неверно";
        }
        Optional<User> userName = userRepository.findByName(name);
        Optional<User> userEmail = userRepository.findByEmail(email);
        if (userName.isPresent()) {
            return "такое имя уже существует";
        }
        if (userEmail.isPresent()) {
            return "Этот e-mail уже зарегистрирован";
        }
        if (password.length() < 6) {
            return "Пароль короче 6-ти символов";
        }
        Optional<CaptchaCode> captchaCode = captchaCodeRepository
                .findBySecretCodeEquals(captchaSecret);
        if (captchaCode.isPresent()) {
            if (captchaCode.get().getCode().equals(captcha)) {
                User newUser = new User();
                newUser.setRegistrationTime(LocalDateTime.now());
                newUser.setName(name);
                newUser.setEmail(email);
                newUser.setPassword(password);
                createNewUser(newUser);
                return "все верно,пользователь создан";
            } else {
                return "Код с картинки введён неверно";
            }
        }

        return "";
    }

    private void createNewUser(User newUser) {
        userRepository.save(newUser);
    }
}
