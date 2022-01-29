package main.controllers;
import main.api.request.LoginRequest;
import main.api.request.RegisterRequest;
import main.api.response.LoginResponse;
import main.api.response.CaptchaResponse;
import main.api.response.RegisterResponse;
import main.service.AuthService;
import main.service.CaptchaService;
import main.service.RegisterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

//@RequestMapping(/api/auth/)  предположительно, здесь будет все,что связано с auth
@Controller
public class ApiAuthController {

    private final CaptchaService captchaService;
    private final RegisterService registerService;
    private final AuthService authService;

    public ApiAuthController(CaptchaService captchaService,
                             RegisterService registerService, AuthService authService) {
        this.captchaService = captchaService;
        this.registerService = registerService;
        this.authService = authService;
    }

    @GetMapping("/api/auth/check")
    public ResponseEntity<LoginResponse> authCheck(Principal principal) {

        if (principal == null) {
            return new ResponseEntity<>(new LoginResponse(),HttpStatus.OK);
        }

        return new ResponseEntity<>(authService.getLoginResponse(principal.getName()),
                HttpStatus.OK);
    }

    @PostMapping("/api/auth/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {

        return new ResponseEntity<>(authService.getLogin(loginRequest.getEmail(),
                loginRequest.getPassword()), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @GetMapping("/api/auth/logout")
    public ResponseEntity<Boolean> logout() {

        return new ResponseEntity<>(authService.getLogout(),HttpStatus.OK);
    }

    @RequestMapping(value = "/api/auth/register", method = { RequestMethod.GET, RequestMethod.POST })
    public ResponseEntity authRegister(
            @RequestBody RegisterRequest registerRequest) throws Exception {

        String response = registerService.checkData(
                registerRequest.getEmail(),
                registerRequest.getPassword(),
                registerRequest.getName(),
                registerRequest.getCaptcha(),
                registerRequest.getSecretCode());
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

    @RequestMapping(value = "/api/auth/captcha", method = { RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE})
    public ResponseEntity<CaptchaResponse> authCaptcha() throws Exception {
        captchaService.deleteOldCaptchasFromRepository();

        return new ResponseEntity<CaptchaResponse>(
                captchaService.generateAndGetCaptcha(),
                HttpStatus.OK);
    }


}
