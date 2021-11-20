package main.api.response;
import lombok.Data;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Data
public class CalendarResponse {

    private List<Integer> years;
    private JSONObject posts;
}
