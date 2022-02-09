package main.api.response;
import lombok.Data;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Data
public class PostResultResponse {

    private boolean result;
    private List<String> description;
}
