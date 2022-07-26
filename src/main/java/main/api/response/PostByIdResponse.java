package main.api.response;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import main.dto.PostByIdDTO;
import org.springframework.stereotype.Component;

@Component
@Data
public class PostByIdResponse {

    @JsonProperty("")
    private PostByIdDTO postData;
}
