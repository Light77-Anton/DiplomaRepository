package main.api.response;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class CaptchaResponse extends Response {

    private String secret;
    private String image;
}
