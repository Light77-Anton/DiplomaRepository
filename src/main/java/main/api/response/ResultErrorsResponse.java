package main.api.response;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Data
public class ResultErrorsResponse {

    private boolean result;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> errors;
}
