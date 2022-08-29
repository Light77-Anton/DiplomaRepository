package main.controllers;
import main.api.request.LoginRequest;
import main.api.request.PasswordRequest;
import main.api.request.RegisterRequest;
import main.api.request.StringRequest;
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

@RequestMapping("/api/auth/")
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

    @GetMapping("check")
    public ResponseEntity<LoginResponse> authCheck(Principal principal) {

        if (principal == null) {
            return ResponseEntity.ok(new LoginResponse());
        }

        return ResponseEntity.ok(authService.getLoginResponse(principal.getName()));
    }

    @PostMapping("login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {

        return ResponseEntity.ok(authService.getLogin(loginRequest.getEmail(), loginRequest.getPassword()));
    }

    @PreAuthorize("hasAuthority('user:write')")
    @GetMapping("logout")
    public ResponseEntity<ResultErrorsResponse> logout() {
        ResultErrorsResponse resultErrorsResponse = new ResultErrorsResponse();
        resultErrorsResponse.setResult(authService.getLogout());

        return ResponseEntity.ok(resultErrorsResponse);
    }

    @PostMapping("register")
    public ResponseEntity<ResultErrorsResponse> authRegister(
            @RequestBody RegisterRequest registerRequest) throws Exception {
        if (!settingsService.isMultiuserMode()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        ResultErrorsResponse resultErrorsResponse = registerService.checkData(registerRequest);
        if (!resultErrorsResponse.getErrors().isEmpty()) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultErrorsResponse);
        }

        return ResponseEntity.ok(resultErrorsResponse);
    }

    @GetMapping("captcha")
    public ResponseEntity<CaptchaResponse> authCaptcha() throws Exception {
        captchaService.deleteOldCaptchasFromRepository();

        return ResponseEntity.ok(captchaService.generateAndGetCaptcha());
    }

    @PostMapping("restore")
    public ResponseEntity<ResultErrorsResponse> authRestore(@RequestBody StringRequest email) {

        return ResponseEntity.ok(authService.checkEmailAndGetCode(email.getEmail()));
    }

    @PostMapping("password")
    public ResponseEntity<ResultErrorsResponse> changePassword(@RequestBody PasswordRequest passwordRequest) {
        ResultErrorsResponse resultErrorsResponse = new ResultErrorsResponse();
        if (authService.checkPasswordChange(passwordRequest).isEmpty()) {
            resultErrorsResponse.setResult(true);
            return ResponseEntity.ok(resultErrorsResponse);
        }
        resultErrorsResponse.setErrors(authService.checkPasswordChange(passwordRequest));

        return ResponseEntity.ok(resultErrorsResponse);
    }
}
