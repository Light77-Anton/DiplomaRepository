package main.api.response;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class FailMessageResponse {

    private String failMessage;
}
