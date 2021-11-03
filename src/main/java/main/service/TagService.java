package main.service;
import main.api.response.TagResponse;
import org.springframework.stereotype.Service;

@Service
public class TagService {

    String query;

    public TagService(){

    }

    public TagService(String query){
        this.query = query;
    }

    public TagResponse getTagList(){

        TagResponse tagResponse = new TagResponse();
        tagResponse.setName("пока никаких тэгов нет");
        return tagResponse;
    }
}
