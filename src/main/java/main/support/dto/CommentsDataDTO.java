package main.support.dto;
import lombok.Data;
import org.json.JSONObject;
import java.time.LocalDate;

@Data
public class CommentsDataDTO {

    private int id;
    private LocalDate timestamp;
    private String text;
    private JSONObject userData;
}
