package main.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class PostByIdDTO {

    private int id;
    private long timestamp;
    @JsonProperty("active")
    private boolean isActive;
    private UserDataDTO user;
    private String title;
    private String text;
    private int likeCount;
    private int dislikeCount;
    private int viewCount;
    private List<CommentsDataDTO> comments;
    private List<String> tags;
}
