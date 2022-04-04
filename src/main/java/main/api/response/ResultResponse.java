package main.api.response;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ResultResponse extends Response {

    private boolean result;
}
