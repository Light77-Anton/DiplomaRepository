package main.api.response;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class CommentSuccessResponse extends Response {

    private int id;
}
