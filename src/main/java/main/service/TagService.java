package main.service;
import main.api.response.TagResponse;
import org.springframework.stereotype.Service;

@Service
public class TagService {

    /*
    String query;

    public TagService(String query){
        this.query = query;
    }
     */

    public TagResponse getTagList() {
        return new TagResponse();
    }
}
