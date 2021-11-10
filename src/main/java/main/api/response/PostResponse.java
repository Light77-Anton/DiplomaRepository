package main.api.response;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class PostResponse {

    @Value("${posts.count}")
    private int count;
    @Value("${posts.query}")
    private String posts;
    @Value("${posts.offset}")
    private int offset;
    @Value("${posts.limit}")
    private int limit;
    @Value("${posts.mode}")
    private String mode;
    /*
    @Value("${postsList}")
    JSONArray posts;
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
