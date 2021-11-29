package main.api.request;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class RegisterRequest {

    @JsonProperty("e_mail")
    private String email;
    @JsonProperty("password")
    private String password;
    @JsonProperty("name")
    private String name;
    @JsonProperty("captcha")
    private String captcha;
    //private String captchaSecret; пока точно непонятно откуда передавать secretCode
}
