package main.controllers;
import main.api.response.CaptchaResponse;
import main.service.CaptchaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ApiAuthController {

    private CaptchaService captchaService;

    public ApiAuthController(CaptchaService captchaService) {
        this.captchaService = captchaService;
    }

    @GetMapping("/api/auth/captcha")
    private ResponseEntity<CaptchaResponse> authCaptcha() throws Exception {
        captchaService.deleteOldCaptchasFromRepository();

        return new ResponseEntity<CaptchaResponse>(
                captchaService.generateAndGetCaptcha(),
                HttpStatus.OK);
    }


}
