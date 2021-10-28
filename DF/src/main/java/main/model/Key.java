package main.model;
import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;

@Data
public class Key implements Serializable{

    public Key(int postId,int tagId){

        this.postId = postId;
        this.tagId = tagId;
    }

    @Column(name = "post_id")
    private int postId;

    @Column(name = "tag_id")
    private int tagId;
}
