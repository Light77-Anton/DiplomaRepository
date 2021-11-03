package main.api.response;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class AuthCheckResponse {

    @Value("${authorization.result}")
    boolean result;
    @Value("${authorization.user.id}")
    int id;
    @Value("${authorization.user.name}")
    String name;
    @Value("${authorization.user.photo}")
    String photo;
    @Value("${authorization.user.email}")
    String email;
    @Value("${authorization.user.moderation}")
    boolean moderation;
    @Value("${user.moderationCount}")
    int moderationCount;
    @Value("${authorization.user.settings}")
    boolean settings;
}
