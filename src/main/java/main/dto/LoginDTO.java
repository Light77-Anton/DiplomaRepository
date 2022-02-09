package main.dto;
import lombok.Data;

@Data
public class LoginDTO {

    private Integer id;
    private String name;
    private String photo;
    private String email;
    private boolean moderation;
    private Integer moderationCount;
    private boolean settings;

}
