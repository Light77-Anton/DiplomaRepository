package main.service;
import main.api.response.TagResponse;
import main.model.Tag;
import main.model.TagToPost;
import main.model.repositories.PostRepository;
import main.model.repositories.TagRepository;
import main.dto.TagDTO;
import main.model.repositories.TagToPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private TagToPostRepository tagToPostRepository;

    public TagResponse getTagList(String query) {
        TagResponse tagResponse = new TagResponse();
        List<Tag> tags;
        if (query == null) {
            tags = tagRepository.findAll();
            tagResponse.setTags(fillAndGetTagsList(tags));
            return tagResponse;
        }
        tags = tagRepository.findAllByNameContaining(query);
        if (tags.isEmpty()) {
            return tagResponse;
        }
        tagResponse.setTags(fillAndGetTagsList(tags));
        return tagResponse;
    }

    private List<TagDTO> fillAndGetTagsList(List<Tag> tags) {
        List<TagDTO> list = new ArrayList<>();
        if (tags.isEmpty()) {
            return list;
        }
        double postCount = postRepository.findPostsCount();
        HashMap<Integer, Integer> map = new HashMap<>();
        for (TagToPost tagToPost : tagToPostRepository.findAll()) {
            if (map.containsKey(tagToPost.getTagId())) {
                int currentCount = map.get(tagToPost.getTagId()) + 1;
                map.put(tagToPost.getTagId(), currentCount);
            } else {
                map.put(tagToPost.getTagId(), 1);
            }
        }
        int countOfTheMostPopularTag = map.values().stream().max(Integer::compare).orElse(0);
        for (Tag tag : tags) {
            TagDTO tagDTO = new TagDTO();
            tagDTO.setName(tag.getName());
            tagDTO.setWeight(tagRepository.getIrrationedWeightByTagName(tag.getName(), postCount)
                    * (1 /(countOfTheMostPopularTag / postCount)));
            list.add(tagDTO);
        }

        return list;
    }

    public void checkAndDeleteUnusedTags() {
        for (Tag tag : tagRepository.findAll()) {
            if (tagToPostRepository.findAllByTagId(tag.getId()).isEmpty()) {
                tagRepository.delete(tag);
            }
        }
    }
}
