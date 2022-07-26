package main.api.response;
import lombok.Data;
import main.dto.LoginDTO;
import org.springframework.stereotype.Component;

@Component
@Data
public class LoginResponse {

    private boolean result;
    private LoginDTO user;

}
