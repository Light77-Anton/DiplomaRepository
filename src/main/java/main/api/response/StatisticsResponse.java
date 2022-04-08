package main.api.response;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class StatisticsResponse {

    private int postsCount;
    private int likesCount;
    private int dislikesCount;
    private int viewsCount;
    private long firstPublication;
}
