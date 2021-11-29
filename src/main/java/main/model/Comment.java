package main.model;
import lombok.Data;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    //@ManyToOne(cascade = CascadeType.ALL)
    @Column(name = "post_id", insertable = false, updatable = false)
    private int postId;

    //@ManyToOne(cascade = CascadeType.ALL)
    @Column(name = "user_id", insertable = false, updatable = false)
    private int userId;

    //@Temporal(TemporalType.TIMESTAMP)
    private LocalDate time;

    private String text;

    /*
    @ManyToOne(cascade = CascadeType.ALL)
    private User user;

    @ManyToOne(cascade = CascadeType.ALL)
    private Post post;

     */
}
