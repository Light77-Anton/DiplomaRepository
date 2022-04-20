package main.model;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Table(name = "tag2post")
@Getter
@Setter
@NoArgsConstructor
public class TagToPost{

    @EmbeddedId
    private Key id;

    @Column(name = "post_id", insertable = false, updatable = false, nullable = false)
    private int postId;

    @Column(name = "tag_id", insertable = false, updatable = false, nullable = false)
    private int tagId;
}
