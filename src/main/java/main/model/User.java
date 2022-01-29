package main.model;
import lombok.Data;
import main.support.Role;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "is_moderator")
    private boolean isModerator;

    @Column(name = "reg_time")
    private LocalDateTime registrationTime;

    private String name;

    private String email;

    private String password;

    private String code;

    private String photo;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Post> posts;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userId")
    private List<Vote> votes;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userId")
    private List<Comment> comments;

    public Role getRole() {
        return isModerator == false ? Role.USER : Role.MODERATOR;
    }
}
