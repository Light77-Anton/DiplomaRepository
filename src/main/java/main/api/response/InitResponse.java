package main.api.response;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class InitResponse {

    @Value("${blog.title}")
    String title;
    @Value("${blog.subtitle}")
    String subtitle;
    @Value("${blog.phone}")
    String phone;
    @Value("${blog.email}")
    String email;
    @Value("${blog.copyright}")
    String copyright;
    @Value("${blog.copyrightFrom}")
    String copyrightFrom;
}
