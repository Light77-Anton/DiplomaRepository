package main.service;
import main.api.response.PostResponse;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    /*
    Integer offset;
    Integer limit;
    String mode;

    List<String> modes = List.of("recent","popular","best","early"); // может заменить на Enum

    public PostService(Integer offset,Integer limit,String mode){
        this.offset = offset;
        this.limit = limit;
        this.mode = mode;
    }

    private void setParams(){
        if(offset == null){
            offset = 0;
        }
        if(!modes.contains(mode)){
            mode = "recent";
        }
    }
     */

    public PostResponse getPostsList() {
        return new PostResponse();
    }
}
