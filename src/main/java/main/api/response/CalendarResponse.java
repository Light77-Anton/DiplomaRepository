package main.api.response;
import lombok.Data;
import org.springframework.stereotype.Component;
import java.util.TreeMap;
import java.util.TreeSet;

@Component
@Data
public class CalendarResponse extends Response {

    private TreeSet<Integer> years;
    private TreeMap<String, Integer> posts;
}
