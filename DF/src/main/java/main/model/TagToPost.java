package main.model;
import lombok.Data;
import javax.persistence.*;

@Entity
@Table(name = "tag2post")
@Data
public class TagToPost{

    @EmbeddedId
    private Key id;

    @Column(name = "post_id")
    private int postId;

    @Column(name = "tag_id")
    private int tagId;
}
