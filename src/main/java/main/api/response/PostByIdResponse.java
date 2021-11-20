package main.api.response;
import lombok.Data;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
@Data
public class PostByIdResponse {

    private JSONObject postData;
}
