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
public class RegisterRequest {

    @JsonProperty("e_mail")
    private String email;
    @JsonProperty("password")
    private String password;
    @JsonProperty("name")
    private String name;
    @JsonProperty("captcha")
    private String captcha;
    @JsonProperty("captcha_secret")
    private String secretCode;
}
