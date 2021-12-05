package main.api.response;
import lombok.Data;
import main.support.dto.PostDTO;
import org.json.JSONArray;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
public class PostResponse {

    private long count;
    private List<PostDTO> posts;
}
