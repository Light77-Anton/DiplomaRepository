package main.model;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Table(name = "tag2post")
@Getter
@Setter
public class TagToPost{

    public TagToPost() {

    }

    public TagToPost(int postId, int tagId) {
        this.postId = postId;
        this.tagId = tagId;
    }

    @EmbeddedId
    private TagToPostId id;

    @Column(name = "post_id", insertable = false, updatable = false, nullable = false)
    private int postId;

    @Column(name = "tag_id", insertable = false, updatable = false, nullable = false)
    private int tagId;
}
