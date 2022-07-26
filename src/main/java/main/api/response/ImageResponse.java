package main.api.response;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Data
public class ImageResponse {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("")
    private String image;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean result;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> errors;
}
