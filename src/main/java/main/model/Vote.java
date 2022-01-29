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

    @Column(name = "user_id", insertable = false, updatable = false)
    private int userId;

    @Column(name = "post_id", insertable = false, updatable = false)
    private int postId;

    private LocalDateTime time;

    private int value;

}
