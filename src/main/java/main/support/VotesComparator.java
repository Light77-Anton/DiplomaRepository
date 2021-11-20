package main.support;
import main.model.Post;
import main.model.Vote;
import java.util.Comparator;

public class VotesComparator implements Comparator<Post> {

    @Override
    public int compare(Post o1, Post o2) {
        int o1LikesCount = 0;
        int o2LikesCount = 0;
        for (Vote vote : o1.getVotes()) {
            if(vote.getValue() == 1){
                o1LikesCount++;
            }
        }
        for (Vote vote : o2.getVotes()) {
            if(vote.getValue() == 1){
                o2LikesCount++;
            }
        }

        if (o1LikesCount > o2LikesCount) {
            return 1;
        }
        else if (o2LikesCount > o1LikesCount) {
            return -1;
        }
        else {
            return 0;
        }
    }
}
