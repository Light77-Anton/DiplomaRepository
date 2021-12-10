package main.support.dto;
import lombok.Data;


@Data
public class PostDTO {

    private int postId;
    private long timestamp;
    private UserDataDTO userData;
    private String title;
    private String announce;
    private int likesCount;
    private int dislikeCount;
    private int commentCount;
    private int viewCount;

}
