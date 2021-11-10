package main.model;
import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;

@Data
public class Key implements Serializable {

    public Key(int postId, int tagId) {

        this.postId = postId;
        this.tagId = tagId;
    }

    @Column(name = "post_id", insertable = false, updatable = false)
    private int postId;

    @Column(name = "tag_id", insertable = false, updatable = false)
    private int tagId;
}
