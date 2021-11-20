package main.controllers;
import main.api.request.RegisterRequest;
import main.api.response.CaptchaResponse;
import main.api.response.RegisterResponse;
import main.service.CaptchaService;
import main.service.RegisterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class ApiPostController {

    private RegisterService registerService;
    private CaptchaService captchaService;

    public ApiPostController(RegisterService registerService,
                             CaptchaService captchaService) {
        this.registerService = registerService;
        this.captchaService = captchaService;
    }

    @PostMapping("/api/auth/register")
    private ResponseEntity authRegister(
            @RequestBody RegisterRequest registerRequest) throws Exception {

        CaptchaResponse captchaResponse = captchaService.generateAndGetCaptcha();
        String response = registerService.checkData(registerRequest.getEmail(),
                registerRequest.getPassword(),
                registerRequest.getName(),
                registerRequest.getCaptcha(),
                captchaResponse.getSecret());
        RegisterResponse registerResponse = new RegisterResponse();
        registerResponse.setDescription(response);
        if (!response.equals("все верно,пользователь создан")) {
            registerResponse.setResult(false);

            return new ResponseEntity(registerResponse, HttpStatus.BAD_REQUEST);
        }
        registerResponse.setResult(true);

        return new ResponseEntity(registerResponse, HttpStatus.OK);
    }


}
