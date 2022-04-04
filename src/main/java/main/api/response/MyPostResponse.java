package main.api.response;
import lombok.Data;
import main.dto.MyPostDTO;
import org.springframework.stereotype.Component;
import java.util.List;

@Data
@Component
public class MyPostResponse extends Response {

    private long count;
    private List<MyPostDTO> posts;
}
