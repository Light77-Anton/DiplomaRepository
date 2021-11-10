package main.model;
import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "post_votes")
@Data
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "user_id", insertable = false, updatable = false)
    private int userId;

    @Column(name = "post_id", insertable = false, updatable = false)
    private int postId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date time;

    private int value;

    @ManyToOne(cascade = CascadeType.ALL)
    private Post post;

    @ManyToOne(cascade = CascadeType.ALL)
    private User user;
}
