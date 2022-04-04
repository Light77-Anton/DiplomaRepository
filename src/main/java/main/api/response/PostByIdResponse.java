package main.api.response;
import lombok.Data;
import main.dto.PostByIdDTO;
import org.springframework.stereotype.Component;

@Component
@Data
public class PostByIdResponse extends Response {

    private PostByIdDTO postData;
}
