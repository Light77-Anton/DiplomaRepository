package main.service;
import main.api.response.CalendarResponse;
import main.model.Post;
import main.model.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class CalendarService {

    @Autowired
    private PostRepository postRepository;

    private final String FORMAT_FOR_DATE = "yyyy-MM-dd";

    private String checkAndGetYear(String year) {
        if (year == null) {
            SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_FOR_DATE);
            String date = sdf.format(new Date());
            year = date.substring(0, 4);
        }

        return year;
    }

    public CalendarResponse getPostsPerYear(String stringYear) {
        String year = checkAndGetYear(stringYear);
        CalendarResponse calendarResponse = new CalendarResponse();
        List<Post> postsList = postRepository.findByYear(year);
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
