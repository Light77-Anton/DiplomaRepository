package main.controllers;
import main.api.request.LoginRequest;
import main.api.request.PasswordRequest;
import main.api.request.RegisterRequest;
import main.api.request.RestoreRequest;
import main.api.response.*;
import main.service.AuthService;
import main.service.CaptchaService;
import main.service.RegisterService;
import main.service.SettingsService;
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
    private final SettingsService settingsService;

    public ApiAuthController(CaptchaService captchaService,
                             RegisterService registerService, AuthService authService, SettingsService settingsService) {
        this.captchaService = captchaService;
        this.registerService = registerService;
        this.authService = authService;
        this.settingsService = settingsService;
    }

    @GetMapping("/api/auth/check")
    public ResponseEntity<LoginResponse> authCheck(Principal principal) {

        if (principal == null) {
            return ResponseEntity.ok(new LoginResponse());
        }

        return ResponseEntity.ok(authService.getLoginResponse(principal.getName()));
    }

    @PostMapping("/api/auth/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {

        return ResponseEntity.ok(authService.getLogin(loginRequest.getEmail(), loginRequest.getPassword()));
    }

    @PreAuthorize("hasAuthority('user:write')")
    @GetMapping("/api/auth/logout")
    public ResponseEntity<Boolean> logout() {

        return ResponseEntity.ok(authService.getLogout());
    }

    @PostMapping("/api/auth/register")
    public ResponseEntity authRegister(
            @RequestBody RegisterRequest registerRequest) throws Exception {
        if (!settingsService.isMultiuserMode()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        RegisterResponse registerResponse = registerService.checkData(registerRequest);
        if (!registerResponse.getDescription().equals("все верно,пользователь создан")) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(registerResponse);
        }

        return ResponseEntity.ok(registerResponse);
    }

    @GetMapping("/api/auth/captcha")
    public ResponseEntity<CaptchaResponse> authCaptcha() throws Exception {
        captchaService.deleteOldCaptchasFromRepository();

        return ResponseEntity.ok(captchaService.generateAndGetCaptcha());
    }

    @PostMapping("/api/auth/restore")
    public ResponseEntity authRestore(@RequestBody RestoreRequest restoreRequest) {

        return ResponseEntity.ok(authService.checkEmailAndGetCode(restoreRequest));
    }

    @PostMapping("/api/auth/password")
    public ResponseEntity changePassword(@RequestBody PasswordRequest passwordRequest) {
        if (authService.checkPasswordChange(passwordRequest).isEmpty()) {
            ResultResponse resultResponse = new ResultResponse();
            resultResponse.setResult(true);
            return ResponseEntity.ok(resultResponse);
        }
        FalseResultErrorsResponse falseResultErrorsResponse = new FalseResultErrorsResponse();
        falseResultErrorsResponse.setErrors(authService.checkPasswordChange(passwordRequest));

        return ResponseEntity.ok(falseResultErrorsResponse);
    }
}
