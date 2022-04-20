package main.model;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class Key implements Serializable {

    public Key(int postId, int tagId) {

        this.postId = postId;
        this.tagId = tagId;
    }

    @Column(name = "post_id", insertable = false, updatable = false, nullable = false)
    private int postId;

    @Column(name = "tag_id", insertable = false, updatable = false, nullable = false)
    private int tagId;
}
