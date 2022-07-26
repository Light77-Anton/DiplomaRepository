package main.dto;
import lombok.Data;

@Data
public class PostDTO {

    private int id;
    private long timestamp;
    private UserDataDTO user;
    private String title;
    private String announce;
    private int likeCount;
    private int dislikeCount;
    private int commentCount;
    private int viewCount;

}
