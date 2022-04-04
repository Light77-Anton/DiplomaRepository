package main.api.response;
import lombok.Data;
import main.dto.PostDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
public class PostResponse extends Response {

    private long count;
    private List<PostDTO> posts;
}
