package main.dto;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MyPostDTO {

    private int postId;
    private LocalDateTime timestamp;
    private String title;
    private String announce;
    private int likeCount;
    private int dislikeCount;
    private int commentCount;
    private int viewCount;
    private UserDataDTO userData;
}
