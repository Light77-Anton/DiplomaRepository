package main.support;
import main.model.Post;
import java.util.Comparator;

public class TimestampComparator implements Comparator<Post> {

    @Override
    public int compare(Post o1, Post o2) {

        if (o1.getTime().isAfter(o2.getTime())) {
            return 1;
        } else if (o2.getTime().isAfter(o1.getTime())) {
            return -1;
        } else {
            return 0;
        }
    }
}
