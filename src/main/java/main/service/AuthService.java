package main.service;
import main.api.request.PasswordRequest;
import main.api.response.LoginResponse;
import main.api.response.ResultErrorsResponse;
import main.config.SecurityConfig;
import main.model.CaptchaCode;
import main.model.repositories.CaptchaCodeRepository;
import main.model.repositories.PostRepository;
import main.model.repositories.UserRepository;
import main.dto.LoginDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthService {

    private static final String CHANGE_PASSWORD = "/login/change-password/";
    private final AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private CaptchaCodeRepository captchaCodeRepository;
    @Autowired
    private SecurityConfig securityConfig;

    public AuthService(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public LoginResponse getLoginResponse(String email) {
        main.model.User currentUser = userRepository.findByEmail
                (email).orElseThrow(() -> new UsernameNotFoundException(email));
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setResult(true);
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setId(currentUser.getId());
        loginDTO.setName(currentUser.getName());
        loginDTO.setEmail(currentUser.getEmail());
        loginDTO.setPhoto(currentUser.getPhoto());
        loginDTO.setModeration(currentUser.isModerator());
        if (currentUser.isModerator()) {
            loginDTO.setModerationCount(postRepository.findAllNewPostsAsList().size());
        }
        else {
            loginDTO.setModerationCount(null);
        }
        loginDTO.setSettings(false);
        loginResponse.setUser(loginDTO);

        return loginResponse;
    }

    public LoginResponse getLogin(String loginEmail, String loginPassword) {
        Authentication auth = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken
                        (loginEmail, loginPassword));
        SecurityContextHolder.getContext().setAuthentication(auth);
        User user = (User) auth.getPrincipal();

        return getLoginResponse(user.getUsername());
    }

    public boolean getLogout() {
        SecurityContextHolder.clearContext();

        return true;
    }

    public ResultErrorsResponse checkEmailAndGetCode(String email) {
        ResultErrorsResponse resultErrorsResponse = new ResultErrorsResponse();
        if (email == null) {
            return resultErrorsResponse;
        }
        Optional<main.model.User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom("testEmailForApp77@gmail.com");
            simpleMailMessage.setTo(email);
            simpleMailMessage.setSubject("Код для восстановления пароля");
            char[] availableChars = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
            StringBuilder hash = new StringBuilder();
            Random random = new Random();
            for (int i = 0; i < 45; i++) {
                hash.append(availableChars[random.nextInt(availableChars
                        .length)]);
            }
            simpleMailMessage.setText(CHANGE_PASSWORD + hash.toString());
            javaMailSender.send(simpleMailMessage);
            userRepository.addRestoreCode(user.get().getId(), CHANGE_PASSWORD + hash.toString());
            resultErrorsResponse.setResult(true);
            return resultErrorsResponse;
        }

        return resultErrorsResponse;
    }

    public List<String> checkPasswordChange(PasswordRequest passwordRequest) {
        List<String> errors = new ArrayList<>();
        if (passwordRequest.getPassword() == null || passwordRequest.getCode() == null ||
                passwordRequest.getCaptcha() == null || passwordRequest.getCaptchaSecret() == null) {
            errors.add("Нужно ввести новый пароль,восстановительный код и код с картинки");
            return errors;
        }
        if (passwordRequest.getPassword().length() < 6) {
            errors.add("Пароль короче 6-ти символов");
        }
        Optional<CaptchaCode> captchaCode = captchaCodeRepository
                .findBySecretCodeEquals(passwordRequest.getCaptchaSecret());
        if (captchaCode.isPresent()) {
            if (captchaCode.get().getCode().equals(passwordRequest.getCaptcha())) {
                Optional<main.model.User> user = userRepository.findByCode(passwordRequest.getCode());
                if (user.isPresent()) {
                    userRepository.findByCodeAndUpdatePassword(passwordRequest.getCode(),
                            securityConfig.passwordEncoder().encode(passwordRequest.getPassword()));
                } else {
                    errors.add("Такого кода восстановления не найдено");
                }
            } else {
                errors.add("Код с картинки введен неверно");
            }
        } else {
            errors.add("Неверно введен секретный код капчи");
        }

        return errors;
    }

}
