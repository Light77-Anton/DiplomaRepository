package main.dto;
import lombok.Data;

@Data
public class MyPostDTO {

    private int id;
    private long timestamp;
    private String title;
    private String announce;
    private int likeCount;
    private int dislikeCount;
    private int commentCount;
    private int viewCount;
    private UserDataDTO user;
}
