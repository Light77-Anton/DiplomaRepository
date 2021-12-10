package main.support.dto;
import lombok.Data;

@Data
public class CommentsDataDTO {

    private int id;
    private long timestamp;
    private String text;
    private ExtendedUserDataDTO userData;
}
