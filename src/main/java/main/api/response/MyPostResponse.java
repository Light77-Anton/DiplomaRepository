package main.api.response;
import lombok.Data;
import main.support.dto.MyPostDTO;
import org.springframework.stereotype.Component;
import java.util.List;

@Data
@Component
public class MyPostResponse {

    private long count;
    private List<MyPostDTO> posts;
}
