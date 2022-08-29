package main.service;
import main.api.request.RegisterRequest;
import main.api.response.ResultErrorsResponse;
import main.config.SecurityConfig;
import main.model.CaptchaCode;
import main.model.repositories.CaptchaCodeRepository;
import main.model.User;
import main.model.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RegisterService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CaptchaCodeRepository captchaCodeRepository;
    @Autowired
    private SecurityConfig securityConfig;

    public ResultErrorsResponse checkData(RegisterRequest registerRequest) {

        ResultErrorsResponse resultErrorsResponse = new ResultErrorsResponse();
        List<String> errors = new ArrayList<>();
        if (registerRequest.getEmail() == null || registerRequest.getName() == null
                || registerRequest.getPassword() == null || registerRequest.getCaptcha() == null
        || registerRequest.getSecretCode() == null) {
            errors.add("запрос не принят,нужно обязательно ввести"
                    + " эмэйл,имя,пароль,секретный код капчи и надпись с картинки для регистрации");
            resultErrorsResponse.setErrors(errors);
            return resultErrorsResponse;
        }
        if (!registerRequest.getEmail().contains("@")) {
            errors.add("это явно не эмэйл");
        }
        if (registerRequest.getName().equals("")) {
            errors.add("Имя указано неверно");
        }
        Optional<User> userName = userRepository.findByName(registerRequest.getName());
        Optional<User> userEmail = userRepository.findByEmail(registerRequest.getEmail());
        if (userName.isPresent()) {
            errors.add("такое имя уже существует");
        }
        if (userEmail.isPresent()) {
            errors.add("Этот e-mail уже зарегистрирован");
        }
        if (registerRequest.getPassword().length() < 6) {
            errors.add("Пароль короче 6-ти символов");
        }
        if (!errors.isEmpty()) {
            resultErrorsResponse.setErrors(errors);
            return resultErrorsResponse;
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
                resultErrorsResponse.setResult(true);
                resultErrorsResponse.setErrors(errors);
                return resultErrorsResponse;
            } else {
                errors.add("Код с картинки введён неверно");
                resultErrorsResponse.setErrors(errors);
                return resultErrorsResponse;
            }
        }
        else {
            errors.add("Похоже время существование капчи истекло,нужно сгенерировать новую");
            resultErrorsResponse.setErrors(errors);
            return resultErrorsResponse;
        }
    }

    private void createNewUser(User newUser) {
        userRepository.save(newUser);
    }
}
