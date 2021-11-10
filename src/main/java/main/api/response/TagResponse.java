package main.api.response;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class TagResponse {

    @Value("${posts.query}")
    private String name;
    @Value("${posts.count}")
    private int postCount;
    @Value("${posts.countWithTag}")
    private int postCountWithTag;
    private double rationedWeight = calculateRationedWeight();
    private double irrationedWeight = calculateIrrationedWeight();

    private double calculateRationedWeight() {
        return 0.0;
    }

    private double calculateIrrationedWeight() {
        return (double) postCountWithTag / postCount;
    }
}
