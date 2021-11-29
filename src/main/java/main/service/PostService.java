package main.service;
import main.api.response.PostByIdResponse;
import main.api.response.PostResponse;
import main.model.*;
import main.model.repositories.PostRepository;
import main.model.repositories.TagRepository;
import main.model.repositories.UserRepository;
import main.support.*;
import main.support.dto.CommentsDataDTO;
import main.support.dto.PostByIdDTO;
import main.support.dto.PostDTO;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

@Service
@Configurable
public class PostService {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TagRepository tagRepository;

    public PostService() {

    }

    public PostByIdResponse getPostById(Integer id) {
        PostByIdResponse postByIdResponse = null;
        Optional<Post> post = postRepository.findById(id);
        if (!post.isPresent()) {
            return postByIdResponse;
        }
        postByIdResponse = new PostByIdResponse();
        PostByIdDTO postByIdDTO = new PostByIdDTO();
        postByIdDTO.setPostId(post.get().getId());
        //Instant instant = post.get().getTime().atTime(LocalTime.now()).atZone(ZoneId.systemDefault()).toInstant(); // требует проверки
        postByIdDTO.setTimestamp(post.get().getTime());
        postByIdDTO.setActive(post.get().isActive());
        JSONObject userObj = new JSONObject();
        userObj.put("id", post.get().getUserId());
        userObj.put("name", post.get().getUser().getName());
        postByIdDTO.setUserData(userObj);
        postByIdDTO.setTitle(post.get().getTitle());
        postByIdDTO.setText(post.get().getText());
        postByIdDTO.setLikesCount(getLikesCount(post.get()));
        postByIdDTO.setDislikeCount(getDislikesCount(post.get()));
        postByIdDTO.setViewCount(post.get().getViewCount());
        JSONArray commentsArray = new JSONArray();
        List<Comment> commentList = post.get().getCommentaries();
        for (Comment comment : commentList) {
            CommentsDataDTO commentsDataDTO = new CommentsDataDTO();
            commentsDataDTO.setId(comment.getId());
            //Instant commentInstant = comment.getTime().atTime(LocalTime.now()).atZone(ZoneId.systemDefault()).toInstant(); // требует проверки
            commentsDataDTO.setTimestamp(comment.getTime());
            commentsDataDTO.setText(comment.getText());
            JSONObject commentUserObj = new JSONObject();
            Optional<User> user = userRepository.findById(comment.getUserId());
            commentUserObj.put("id", user.get().getId());
            commentUserObj.put("name", user.get().getName());
            commentUserObj.put("photo", user.get().getPhoto());
            commentsDataDTO.setUserData(commentUserObj);
            commentsArray.put(commentsDataDTO);
        }
        postByIdDTO.setCommentsData(commentsArray);
        JSONArray tagsArray = new JSONArray();
        List<Tag> tagsList = post.get().getTags();
        for (Tag tag : tagsList) {
            tagsArray.put(tag.getName());
        }
        postByIdDTO.setTagsData(tagsArray);
        postByIdResponse.setPostData(postByIdDTO);

        return postByIdResponse;
    }

    public PostResponse getPostsByTag(Integer offset,
                                      Integer limit, String stringTag) {
        Tag requiredTag = tagRepository.findByNameContaining(stringTag);
        PostResponse postResponse = new PostResponse();
        if (requiredTag == null) {
            postResponse.setCount(0);
            postResponse.setPosts(new JSONArray());
            return postResponse;
        }
        Pageable page = PageRequest.of(checkAndGetOffset(offset),
                checkAndGetLimit(limit));
        List<Post> postsList = postRepository.findByTagsContaining(requiredTag, page);
        postResponse.setCount(postsList.size());
        postResponse.setPosts(fillAndGetArrayWithPosts(postsList));

        return postResponse;
    }

    public PostResponse getPostsByDate(Integer offset,
                                       Integer limit, String stringDate) {
        PostResponse postResponse = new PostResponse();
        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd"); возможно нужна доп. проверка,что переданная дата в правильном формате
        List<Post> postsList =
                getPostsListWithRequiredDate(checkAndGetOffset(offset),
                checkAndGetLimit(limit), stringDate);
        postResponse.setCount(postsList.size());
        postResponse.setPosts(fillAndGetArrayWithPosts(postsList));

        return postResponse;
    }

    public PostResponse getPostsListByQuery(Integer offset,
                                            Integer limit, String query) {
        PostResponse postResponse = new PostResponse();
        List<Post> postsList =
                getPostsListWithRequiredQuery(checkAndGetOffset(offset),
                        checkAndGetLimit(limit), query);
        postResponse.setCount(postsList.size());
        postResponse.setPosts(fillAndGetArrayWithPosts(postsList));

        return postResponse;
    }

    public PostResponse getPostsList(Integer offset,
                                     Integer limit, String stringMode) {
        PostResponse postResponse = new PostResponse();
        List<Post> postsList =
                getPostsListWithRequiredMode(checkAndGetOffset(offset),
                        checkAndGetLimit(limit), checkAndGetMode(stringMode));
        postResponse.setCount(postsList.size());
        postResponse.setPosts(fillAndGetArrayWithPosts(postsList));

        return postResponse;
    }

    private JSONArray fillAndGetArrayWithPosts(List<Post> postsList) {
        JSONArray array = new JSONArray();
        for (Post post : postsList) {
            PostDTO postDTO = new PostDTO();
            postDTO.setPostId(post.getId());
            //Instant instant = post.getTime().atTime(LocalTime.now()).atZone(ZoneId.systemDefault()).toInstant(); // требует проверки
            postDTO.setTimestamp(post.getTime());
            JSONObject userObj = new JSONObject();
            userObj.put("id", post.getUserId());
            userObj.put("name", post.getUser().getName());
            postDTO.setUserData(userObj);
            postDTO.setTitle(post.getTitle());
            postDTO.setAnnounce(getAnnounce(post.getText()));
            postDTO.setLikesCount(getLikesCount(post));
            postDTO.setDislikeCount(getDislikesCount(post));
            postDTO.setCommentCount(post.getCommentaries().size());
            postDTO.setViewCount(post.getViewCount());
            array.put(postDTO);
        }
        return array;
    }

    private Mode checkAndGetMode(String stringMode) {
        if (stringMode == null) {
            return Mode.RECENT;
        }
        Mode mode = null;
        boolean isValidMode = false;
        for (Mode modeValue : Mode.values()) {
            if (modeValue.name().equals(stringMode)) {
                mode = modeValue;
                isValidMode = true;
                break;
            }
        }
        if (!isValidMode) {
            mode = Mode.RECENT;
        }

        return mode;
    }

    private Integer checkAndGetOffset(Integer offset) {
        if (offset == null || offset < 0) {
            offset = 0;
        }

        return offset;
    }

    private Integer checkAndGetLimit(Integer limit) {
        if (limit == null || limit < 0) {
            limit = 10;
        }

        return limit;
    }

    private List<Post> checkAndGetPostsList(List<Post> bufferList) {
        List<Post> list = new ArrayList<>();
        for (Post post : bufferList) {
            //Instant instant = post.getTime().atTime(LocalTime.now()).atZone(ZoneId.systemDefault()).toInstant(); // требует проверки
            if (post.isActive()
                    && post.getModerationStatus() == ModerationStatus.ACCEPTED
                    && post.getTime().isAfter(LocalDate.now())) {
                list.add(post);
            }
        }

        return list;
    }

    private List<Post> getPostsListWithRequiredDate(Integer offset,
                                                    Integer limit,
                                                    String stringDate) {
        List<Post> bufferList;
        List<Post> list;
        Pageable page = PageRequest.of(offset, limit);
        bufferList = postRepository.findByTimeEquals(stringDate, page);
        list = checkAndGetPostsList(bufferList);
        return list;
    }

    private List<Post> getPostsListWithRequiredQuery(Integer offset,
                                                     Integer limit,
                                                     String query) {
        List<Post> bufferList;
        List<Post> list;
        Pageable page = PageRequest.of(offset, limit);
        bufferList = postRepository.findByTextContaining(query, page);
        list = checkAndGetPostsList(bufferList);
        list.sort(new TimestampComparator());
        return list;
    }

    private List<Post> getPostsListWithRequiredMode(Integer offset,
                                                    Integer limit, Mode mode) {
        List<Post> bufferList;
        List<Post> list;
        if (mode == Mode.RECENT) {
            Pageable page = PageRequest.of(offset, limit);
            bufferList = postRepository.findAll(page).getContent();
            list = checkAndGetPostsList(bufferList);
            list.sort(new TimestampComparator());
        } else if (mode == Mode.POPULAR) {
            Pageable page = PageRequest.of(offset, limit);
            bufferList = postRepository.findAll(page).getContent();
            list = checkAndGetPostsList(bufferList);
            list.sort(new CommentsComparator());
        } else if (mode == Mode.BEST) {
            Pageable page = PageRequest.of(offset, limit);
            bufferList = postRepository.findAll(page).getContent();
            list = checkAndGetPostsList(bufferList);
            list.sort(new VotesComparator());
        } else { // EARLY
            Pageable page = PageRequest.of(offset, limit);
            bufferList = postRepository.findAll(page).getContent();
            list = checkAndGetPostsList(bufferList);
            list.sort(new TimestampComparator().reversed());
        }

        return list;
    }

    private String getAnnounce(String text) {
        String announce;
        if (text.length() < 150) {
            announce = text + "...";
        } else {
            announce = text.substring(0, 150) + "...";
        }
        return announce;
    }

    private int getLikesCount(Post post) {
        int likesCount = 0;
        for (Vote vote : post.getVotes()) {
            if (vote.getValue() == 1) {
                likesCount++;
            }
        }

        return likesCount;
    }

    private int getDislikesCount(Post post) {
        int dislikesCount = 0;
        for (Vote vote : post.getVotes()) {
            if (vote.getValue() == -1) {
                dislikesCount++;
            }
        }

        return dislikesCount;
    }

}
