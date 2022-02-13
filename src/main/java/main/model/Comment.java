package main.model;
import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "post_comments")
@Data
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "parent_id")
    private Integer parentId;

    @Column(name = "post_id", insertable = false, updatable = false)
    private int postId;

    @Column(name = "user_id", insertable = false, updatable = false)
    private int userId;

    private LocalDateTime time;

    private String text;

}
