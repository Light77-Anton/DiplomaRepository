package main.api.response;
import lombok.Data;
import main.support.dto.TagDTO;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Data
public class TagResponse {

    private List<TagDTO> tags;
}
