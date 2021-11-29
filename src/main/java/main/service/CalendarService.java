package main.service;
import main.api.response.CalendarResponse;
import main.model.Post;
import main.model.repositories.PostRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
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
            year = date.substring(0, 5);
        }

        return year;
    }

    public CalendarResponse getPostsPerYear(String stringYear) {
        String year = checkAndGetYear(stringYear);
        CalendarResponse calendarResponse = new CalendarResponse();
        Iterable<Post> posts = postRepository.findByTimeContaining(year);
        List<String> listWithDates = new ArrayList<>();
        List<Integer> listWithYears = new ArrayList<>();
        /**
         * listWithDates - должен хранить только даты указанного года
         * listWithYears - должен хранить все года,когда была хоть 1 пост
         */
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_FOR_DATE);
        for (Post post : posts) {
            String date = simpleDateFormat.format(post.getTime());
            Integer yyyy = Integer.getInteger(date.substring(0, 5));
            if (!listWithYears.contains(yyyy)) {
                listWithYears.add(yyyy);
            }
            if (date.substring(0, 5).equals(year)) {
                listWithDates.add(date);
            }
        }
        listWithYears.sort(Comparator.reverseOrder());
        calendarResponse.setYears(listWithYears);
        Map<String, Integer> map = new TreeMap<>();
        /**
         * ListWithYears сортируем от меньшего к большему
         * В map ключ - дата,а значение - кол-во публикаций за эту дату
         */
        for (String dateWithRequiredYear : listWithDates) {
            if (!map.containsKey(dateWithRequiredYear)) {
                map.put(dateWithRequiredYear, 1);
            } else {
                int count = map.get(dateWithRequiredYear);
                count++;
                map.put(dateWithRequiredYear, count);
            }
        }
        JSONObject insideObj = new JSONObject();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            insideObj.put(entry.getKey(), entry.getValue());
        }
        JSONObject outsideObj = new JSONObject();
        outsideObj.put("posts", insideObj);
        calendarResponse.setPosts(outsideObj);

        return calendarResponse;
    }
}
