package main.support.dto;
import lombok.Data;
import java.util.List;

@Data
public class PostByIdDTO {

    private int postId;
    private long timestamp;
    private boolean isActive;
    private UserDataDTO userData;
    private String title;
    private String text;
    private int likesCount;
    private int dislikeCount;
    private int viewCount;
    private List<CommentsDataDTO> commentsData;
    private List<String> tagsData;
}
