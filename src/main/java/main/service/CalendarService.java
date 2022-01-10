package main.service;
import main.api.response.CalendarResponse;
import main.model.Post;
import main.model.repositories.PostRepository;
import main.support.ModerationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class CalendarService {

    @Autowired
    private PostRepository postRepository;

    private final String FORMAT_FOR_DATE = "yyyy-MM-dd";

    public CalendarService() {

    }

    private String checkAndGetYear(String year) {
        if (year == null) {
            SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_FOR_DATE);
            String date = sdf.format(new Date());
            year = date.substring(0, 4);
        }

        return year;
    }

    private List<Post> checkAndGetPostsList(List<Post> bufferPostsList) {
        List<Post> list = new ArrayList<>();
        for (Post post : bufferPostsList) {
            if (post.isActive()
                    && post.getModerationStatus() == ModerationStatus.ACCEPTED
                    && LocalDateTime.now().isAfter(post.getTime())) {
                list.add(post);
            }
        }

        return list;
    }

    public CalendarResponse getPostsPerYear(String stringYear) {
        String year = checkAndGetYear(stringYear);
        CalendarResponse calendarResponse = new CalendarResponse();
        List<Post> bufferPostsList = postRepository.findByYear(year);
        List<Post> postsList = checkAndGetPostsList(bufferPostsList);
        List<String> listWithDates = new ArrayList<>();
        for (Post post : postsList) {
            String date = post.getTime().toString().substring(0, 4);
            if (date.equals(year)) {
                listWithDates.add(date);
            }
        }
        TreeSet<Integer> setWithYears = postRepository.findAllYears();
        calendarResponse.setYears(setWithYears);
        TreeMap<String, Integer> map = new TreeMap<>();
        for (String dateWithRequiredYear : listWithDates) {
            if (!map.containsKey(dateWithRequiredYear)) {
                map.put(dateWithRequiredYear, 1);
            } else {
                int count = map.get(dateWithRequiredYear);
                count++;
                map.put(dateWithRequiredYear, count);
            }
        }
        calendarResponse.setPosts(map);

        return calendarResponse;
    }
}
