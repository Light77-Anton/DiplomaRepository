package main.model;
import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "post_comments")
@Data
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "parent_id")
    private int parentId;

    @Column(name = "post_id")
    private int postId;

    @Column(name = "user_id")
    private int userId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date time;

    private String text;

    @ManyToOne(cascade = CascadeType.ALL)
    private User user;

    @ManyToOne(cascade = CascadeType.ALL)
    private Post post;
}
