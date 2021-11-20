package main.api.response;
import lombok.Data;
import org.json.JSONArray;
import org.springframework.stereotype.Component;

@Component
@Data
public class PostResponse {

    private long count;
    private JSONArray posts;
}
