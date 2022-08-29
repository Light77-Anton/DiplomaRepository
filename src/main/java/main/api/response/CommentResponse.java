package main.api.response;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;
import java.util.HashMap;

@Component
@Data
public class CommentResponse {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean result;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private HashMap<String, String> errors;
}
