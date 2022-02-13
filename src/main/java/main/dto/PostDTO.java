package main.dto;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PostDTO {

    private int postId;
    private LocalDateTime timestamp;
    private UserDataDTO userData;
    private String title;
    private String announce;
    private int likesCount;
    private int dislikeCount;
    private int commentCount;
    private int viewCount;

}
