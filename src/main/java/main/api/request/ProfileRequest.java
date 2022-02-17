package main.api.request;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileRequest {

    @JsonProperty("name")
    private String name;
    @JsonProperty("e_mail")
    private String email;
    @JsonProperty("password")
    private String password;
    @JsonProperty("photo")
    private String photo;
    @JsonProperty("remove_photo")
    private boolean removePhoto;

}
