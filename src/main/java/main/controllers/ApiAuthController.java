package main.controllers;
import main.api.request.RegisterRequest;
import main.api.response.AuthCheckResponse;
import main.api.response.CaptchaResponse;
import main.api.response.RegisterResponse;
import main.service.CaptchaService;
import main.service.RegisterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
//@RequestMapping(/api/auth/)  предположительно, здесь будет все,что связано с auth
public class ApiAuthController {

    private final CaptchaService captchaService;
    private final RegisterService registerService;
    private final AuthCheckResponse authCheckResponse;

    public ApiAuthController(CaptchaService captchaService,
                             RegisterService registerService,
                             AuthCheckResponse authCheckResponse) {
        this.captchaService = captchaService;
        this.registerService = registerService;
        this.authCheckResponse = authCheckResponse;
    }

    @GetMapping("/api/auth/check")
    private ResponseEntity<AuthCheckResponse> authCheck() {
        return new ResponseEntity<>(authCheckResponse, HttpStatus.OK);
    }

    @PostMapping("/api/auth/register")
    private ResponseEntity authRegister(
            @RequestBody RegisterRequest registerRequest) throws Exception {

        CaptchaResponse captchaResponse = captchaService.
                generateAndGetCaptcha();
        String response = registerService.checkData(registerRequest.getEmail(),
                registerRequest.getPassword(),
                registerRequest.getName(),
                registerRequest.getCaptcha(),
                captchaResponse.getSecret());
        /**
         * не уверен,что такую проверку корректно оставить в контроллере
         */
        RegisterResponse registerResponse = new RegisterResponse();
        registerResponse.setDescription(response);
        if (!response.equals("все верно,пользователь создан")) {
            registerResponse.setResult(false);

            return new ResponseEntity(registerResponse, HttpStatus.BAD_REQUEST);
        }
        registerResponse.setResult(true);

        return new ResponseEntity(registerResponse, HttpStatus.OK);
    }

    @GetMapping("/api/auth/captcha")
    private ResponseEntity<CaptchaResponse> authCaptcha() throws Exception {
        captchaService.deleteOldCaptchasFromRepository();

        return new ResponseEntity<CaptchaResponse>(
                captchaService.generateAndGetCaptcha(),
                HttpStatus.OK);
    }


}
