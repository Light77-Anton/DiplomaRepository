package main.api.response;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class ImageLinkResponse extends Response{

    private String link;
}
