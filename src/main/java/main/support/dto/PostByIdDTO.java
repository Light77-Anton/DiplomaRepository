package main.support.dto;
import lombok.Data;
import org.json.JSONArray;
import org.json.JSONObject;
import java.time.LocalDate;

@Data
public class PostByIdDTO {

    private int postId;
    private LocalDate timestamp;
    private boolean isActive;
    private JSONObject userData;
    private String title;
    private String text;
    private int likesCount;
    private int dislikeCount;
    private int viewCount;
    private JSONArray commentsData;
    private JSONArray tagsData;
}
