package main.model;
import lombok.Data;
import main.support.ModerationStatus;
import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "posts")
@Data
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "is_active")
    private boolean isActive;

    @Enumerated(EnumType.STRING)
    @Column(name = "moderation_status", columnDefinition = "enum")
    private ModerationStatus moderationStatus;

    @Column(name = "moderator_id")
    private int moderatorId;

    @Column(name = "user_id", insertable = false, updatable = false)
    private int userId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date time;

    private String title;

    private String text;

    @Column(name = "view_count")
    private int viewCount;

    @ManyToOne(cascade = CascadeType.ALL)
    private User user;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Vote> votes;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "TagToPost",
            joinColumns = {@JoinColumn(name = "post_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private List<Tag> tags;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Comment> commentaries;
}
