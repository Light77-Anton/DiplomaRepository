package main.support.dto;
import lombok.Data;

@Data
public class MyPostDTO {

    private int postId;
    private long timestamp;
    private String title;
    private String announce;
    private int likeCount;
    private int dislikeCount;
    private int commentCount;
    private int viewCount;
    private UserDataDTO userData;
}
