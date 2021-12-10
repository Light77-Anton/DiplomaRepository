package main.service;
import main.api.response.TagResponse;
import main.model.repositories.PostRepository;
import main.model.Tag;
import main.model.repositories.TagRepository;
import main.support.dto.TagDTO;
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
        TagResponse tagResponse;
        List<Tag> tags;
        if (query == null) {
            tags = tagRepository.findAll();
            tagResponse = new TagResponse();
            tagResponse.setTags(fillAndGetTagsList(tags));
        }
        tagResponse = new TagResponse();
        tags = tagRepository.findAllByNameContaining(query);
        tagResponse.setTags(fillAndGetTagsList(tags));
        return tagResponse;
    }

    private List<TagDTO> fillAndGetTagsList(List<Tag> tags) {
        List<TagDTO> list = new ArrayList<>();
        for (Tag tag : tags) {
            TagDTO tagDTO = new TagDTO();
            tagDTO.setName(tag.getName());
            tagDTO.setWeight(
                    tagRepository.getIrrationedWeightByTagName(tag.getName())
                            * tagRepository.getTheMostPopularTagWeight());
            list.add(tagDTO);
        }

        return list;
    }

    /*
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
     */
}
