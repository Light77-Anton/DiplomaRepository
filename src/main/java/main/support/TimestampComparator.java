package main.support;
import main.model.Post;

import java.util.Comparator;

public class TimestampComparator implements Comparator<Post> {

    @Override
    public int compare(Post o1, Post o2) {
        if (o1.getTime().getTime() > o2.getTime().getTime()) {
            return 1;
        }
        else if (o2.getTime().getTime() > o1.getTime().getTime()) {
            return -1;
        }
        else {
            return 0;
        }
    }
}
