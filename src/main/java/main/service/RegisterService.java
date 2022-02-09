package main.service;
import main.api.request.RegisterRequest;
import main.api.response.RegisterResponse;
import main.config.SecurityConfig;
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
    @Autowired
    private SecurityConfig securityConfig;

    public RegisterResponse checkData(RegisterRequest registerRequest) {

        RegisterResponse registerResponse = new RegisterResponse();
        if (registerRequest.getEmail() == null || registerRequest.getName() == null
                || registerRequest.getPassword() == null || registerRequest.getCaptcha() == null
        || registerRequest.getSecretCode() == null) {
            registerResponse.setResult(false);
            registerResponse.setDescription("запрос не принят,нужно обязательно ввести"
                    + " эмэйл,имя,пароль,секретный код капчи и надпись с картинки для регистрации");
            return registerResponse;
        }
        if (!registerRequest.getEmail().contains("@")) {
            registerResponse.setResult(false);
            registerResponse.setDescription("это явно не эмэйл");
            return registerResponse;
        }
        if (registerRequest.getName().equals("")) {
            registerResponse.setResult(false);
            registerResponse.setDescription("Имя указано неверно");
            return registerResponse;
        }
        Optional<User> userName = userRepository.findByName(registerRequest.getName());
        Optional<User> userEmail = userRepository.findByEmail(registerRequest.getEmail());
        if (userName.isPresent()) {
            registerResponse.setResult(false);
            registerResponse.setDescription("такое имя уже существует");
            return registerResponse;
        }
        if (userEmail.isPresent()) {
            registerResponse.setResult(false);
            registerResponse.setDescription("Этот e-mail уже зарегистрирован");
            return registerResponse;
        }
        if (registerRequest.getPassword().length() < 6) {
            registerResponse.setResult(false);
            registerResponse.setDescription("Пароль короче 6-ти символов");
            return registerResponse;
        }
        Optional<CaptchaCode> captchaCode = captchaCodeRepository
                .findBySecretCodeEquals(registerRequest.getSecretCode());
        if (captchaCode.isPresent()) {
            if (captchaCode.get().getCode().equals(registerRequest.getCaptcha())) {
                User newUser = new User();
                newUser.setRegistrationTime(LocalDateTime.now());
                newUser.setName(registerRequest.getName());
                newUser.setEmail(registerRequest.getEmail());
                newUser.setPassword(securityConfig.passwordEncoder().encode(registerRequest.getPassword()));
                createNewUser(newUser);
                registerResponse.setResult(true);
                registerResponse.setDescription("все верно,пользователь создан");
                return registerResponse;
            } else {
                registerResponse.setResult(false);
                registerResponse.setDescription("Код с картинки введён неверно");
                return registerResponse;
            }
        }
        else {
            registerResponse.setResult(false);
            registerResponse.setDescription("Похоже время время существование капчи истекло" +
                    ",нужно сгенерировать новую");
            return registerResponse;
        }
    }

    private void createNewUser(User newUser) {
        userRepository.save(newUser);
    }
}
