package main.api.response;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class AuthCheckResponse {

    @Value("${blog.authorization.result}")
    private boolean result;
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
}
