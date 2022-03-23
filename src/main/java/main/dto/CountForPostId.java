package main.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CountForPostId {

    int id;
    long count;
}
