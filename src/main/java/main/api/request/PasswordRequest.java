package main.api.request;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordRequest {

    @JsonProperty("code")
    private String code;
    @JsonProperty("password")
    private String password;
    @JsonProperty("captcha")
    private String captcha;
    @JsonProperty("captcha_secret")
    private String captchaSecret;
}
