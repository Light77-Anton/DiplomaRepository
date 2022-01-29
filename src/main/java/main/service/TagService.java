package main.service;
import main.api.response.TagResponse;
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
        double postCount = tagRepository.getPostsCount();
        for (Tag tag : tags) {
            TagDTO tagDTO = new TagDTO();
            tagDTO.setName(tag.getName());
            tagDTO.setWeight(tagRepository.getIrrationedWeightByTagName(tag.getName(),postCount)
                    * (1 / (tagRepository.getPostsCountWithTheMostPopularTag()
                    / tagRepository.getPostsCount())));
            list.add(tagDTO);
        }

        return list;
    }

}
