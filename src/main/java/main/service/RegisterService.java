package main.service;
import main.model.CaptchaCode;
import main.model.CaptchaCodeRepository;
import main.model.User;
import main.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;

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
        Iterable<User> users = userRepository.findAll();
        if (!email.contains("@")) {
            return "это явно не email";
        }
        if (name.equals("")) {
            return "Имя указано неверно";
        }
        for (User user : users) {
            if (user.getName().equals(name)) {
                return "такое имя уже существует";
            }
            if (user.getEmail().equals(email)) {
                return "Этот e-mail уже зарегистрирован";
            }
        }
        if (password.length() < 6) {
            return "Пароль короче 6-ти символов";
        }
        Iterable<CaptchaCode> captchaCodes = captchaCodeRepository.findAll();
        for (CaptchaCode captchaCode : captchaCodes) {
            if (captchaCode.getSecretCode().equals(captchaSecret)) {
                if (captchaCode.getCode().equals(captcha)) {
                    User newUser = new User();
                    newUser.setRegistrationTime(new Date());
                    newUser.setName(name);
                    newUser.setEmail(email);
                    newUser.setPassword(password);
                    createNewUser(newUser);

                    return "все верно,пользователь создан";
                } else {
                    return "Код с картинки введён неверно";
                }
            }
        }

        return "";
    }

    private void createNewUser(User newUser) {
        userRepository.save(newUser);
    }
}
