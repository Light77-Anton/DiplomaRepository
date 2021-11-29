package main.api.response;
import lombok.Data;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class TagResponse {

    private JSONArray tags;

}
