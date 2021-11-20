package main.support;
import main.model.Post;
import java.util.Comparator;

public class CommentsComparator implements Comparator<Post> {
    @Override
    public int compare(Post o1, Post o2) {
        if (o1.getCommentaries().size() > o2.getCommentaries().size()) {
            return 1;
        }
        else if (o2.getCommentaries().size() > o1.getCommentaries().size()) {
            return -1;
        }
        else {
            return 0;
        }
    }
}
