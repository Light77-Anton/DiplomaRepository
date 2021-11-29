package main.api.response;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class AuthCheckResponse {

    private boolean result = false; // пока авторизация пользователя не реализована

}
