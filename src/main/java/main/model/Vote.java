package main.model;
import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "post_votes")
@Data
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    //@ManyToOne(cascade = CascadeType.ALL)
    @Column(name = "user_id", insertable = false, updatable = false)
    private int userId;

    //@ManyToOne(cascade = CascadeType.ALL)
    @Column(name = "post_id", insertable = false, updatable = false)
    private int postId;

    //@Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime time;

    private int value;

    /*
    @ManyToOne(cascade = CascadeType.ALL)
    private Post post;

    @ManyToOne(cascade = CascadeType.ALL)
    private User user;

     */
}
