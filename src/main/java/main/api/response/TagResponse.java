package main.api.response;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
public class TagResponse {

    String name;
    @Value("posts.count")
    int postCount;
    int postCountWithTag;
    double rationedWeight = calculateRationedWeight();
    double irrationedWeight = calculateIrrationedWeight();

    private double calculateRationedWeight(){
        return 0.0;
    }

    private double calculateIrrationedWeight(){
        return (double)postCountWithTag / postCount;
    }
}
