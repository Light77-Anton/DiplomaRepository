package main.service;
import main.api.response.TagResponse;
import main.model.repositories.PostRepository;
import main.model.Tag;
import main.model.repositories.TagRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private PostRepository postRepository;

    public TagService() {

    }

    public TagResponse getTagList(String query) {
        TagResponse tagResponse = new TagResponse();
        List<Tag> tags = tagRepository.findAllByNameContaining(query);
        JSONArray array = new JSONArray();
        for (Tag tag : tags) {
            JSONObject obj = new JSONObject();
            obj.put("name", tag.getName());
            obj.put("weight", calculateAndGetRationedWeight(tags,
                    calculateAndGetIrrationedWeight(tag)));
            array.put(obj);
        }
        tagResponse.setTags(array);

        return tagResponse;
    }

    private double calculateAndGetRationedWeight(Iterable<Tag> tags,
                                                 Double irrationedWeightOfTag) {
        Map<Tag, Double> mapWithWeight = new HashMap();
        for (Tag tag : tags) {
            mapWithWeight.put(tag, calculateAndGetIrrationedWeight(tag));
        }
        Tag theMostPopularTag = null;
        for (Map.Entry<Tag, Double> entry : mapWithWeight.entrySet()) {
            if (theMostPopularTag == null
                    || entry.getValue()
                    > calculateAndGetIrrationedWeight(theMostPopularTag)) {
                theMostPopularTag = entry.getKey();
            }
        }
        double k = 1 / mapWithWeight.get(theMostPopularTag);

        return k * irrationedWeightOfTag;
    }

    private double calculateAndGetIrrationedWeight(Tag tag) {
        double postCountWithTag = tag.getPosts().size();
        double postCount = postRepository.count();

        return postCountWithTag / postCount;
    }
}
