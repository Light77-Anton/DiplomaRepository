package main.api.request;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest {

    @JsonProperty("timestamp")
    private Long timestamp;
    @JsonProperty("active")
    private Byte isActive;
    @JsonProperty("title")
    private String title;
    @JsonProperty("tags")
    private List<String> tags;
    @JsonProperty("text")
    private String text;
}
