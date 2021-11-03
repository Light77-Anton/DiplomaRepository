package main.api.response;
import lombok.Data;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Value;
import java.text.SimpleDateFormat;

@Data
public class PostResponse {

    @Value("posts")
    JSONArray posts;
    /*
    @Value("posts.count")
    int count;
    @Value("posts.post.id")
    int postId;
    @Value("posts.post.timestamp")
    SimpleDateFormat timestamp;
    @Value("posts.post.user.id")
    int userId;
    @Value("posts.post.user.name")
    String userName;
    @Value("posts.post.title")
    String title;
    @Value("posts.post.announce")
    String announce;
    @Value("posts.post.likeCount")
    int likeCount;
    @Value("posts.post.dislikeCount")
    int dislikeCount;
    @Value("posts.post.commentCount")
    int commentCount;
    @Value("posts.post.viewCount")
    int viewCount;
     */
}
