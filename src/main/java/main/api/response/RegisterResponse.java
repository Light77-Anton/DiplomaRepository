package main.api.response;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class RegisterResponse {

    private boolean result;
    private String description;
}
