package main.api.response;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class AuthCheckResponse {

    private boolean result = false; // пока авторизация пользователя не реализована
    /*
    @Value("${blog.authorization.user.id}")
    private int id;
    @Value("${blog.authorization.user.name}")
    private String name;
    @Value("${blog.authorization.user.photo}")
    private String photo;
    @Value("${blog.authorization.user.email}")
    private String email;
    @Value("${blog.authorization.user.moderation}")
    private boolean moderation;
    @Value("${blog.authorization.user.moderationCount}")
    private int moderationCount;
    @Value("${blog.authorization.user.settings}")
    private boolean settings;
     */
}
