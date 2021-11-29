package main.support.dto;
import lombok.Data;
import org.json.JSONObject;
import java.time.LocalDate;

@Data
public class PostDTO {

    private int postId;
    private LocalDate timestamp;
    private JSONObject userData;
    private String title;
    private String announce;
    private int likesCount;
    private int dislikeCount;
    private int commentCount;
    private int viewCount;

}
