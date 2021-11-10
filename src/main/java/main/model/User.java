package main.model;
import lombok.Data;
import javax.persistence.*;
import java.util.Date;
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
    @Temporal(TemporalType.TIMESTAMP)
    private Date registrationTime;

    private String name;

    private String email;

    private String password;

    private String code;

    private String photo;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Post> posts;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Vote> votes;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Comment> comments;

    @OneToMany(cascade = CascadeType.ALL)
    private List<CaptchaCode> captchaCodes;
}