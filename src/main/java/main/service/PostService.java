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
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import java.time.*;
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
        postByIdDTO.setTimestamp(post.get().getTime().toEpochSecond(ZoneOffset.UTC));
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
        List<CommentsDataDTO> commentsDataDTOList = new ArrayList<>();
        List<Comment> commentList = post.get().getCommentaries();
        for (Comment comment : commentList) {
            CommentsDataDTO commentsDataDTO = new CommentsDataDTO();
            commentsDataDTO.setId(comment.getId());
            commentsDataDTO.setTimestamp(comment.getTime().toEpochSecond(ZoneOffset.UTC));
            commentsDataDTO.setText(comment.getText());
            JSONObject commentUserObj = new JSONObject();
            Optional<User> user = userRepository.findById(comment.getUserId());
            commentUserObj.put("id", user.get().getId());
            commentUserObj.put("name", user.get().getName());
            commentUserObj.put("photo", user.get().getPhoto());
            commentsDataDTO.setUserData(commentUserObj);
            commentsDataDTOList.add(commentsDataDTO);
        }
        postByIdDTO.setCommentsData(commentsDataDTOList);
        List<String> tagsNamesList = new ArrayList<>();
        List<Tag> tagsList = post.get().getTags();
        for (Tag tag : tagsList) {
            tagsNamesList.add(tag.getName());
        }
        postByIdDTO.setTagsData(tagsNamesList);
        postByIdResponse.setPostData(postByIdDTO);

        return postByIdResponse;
    }

    public PostResponse getPostsByTag(Integer offset,
                                      Integer limit, String stringTag) {
        Tag requiredTag = tagRepository.findByNameContaining(stringTag);
        PostResponse postResponse = new PostResponse();
        if (requiredTag == null) {
            postResponse.setCount(0);
            postResponse.setPosts(new ArrayList<>());
            return postResponse;
        }
        Pageable page = PageRequest.of(checkAndGetOffset(offset),
                checkAndGetLimit(limit));
        Page<Post> postsPage = postRepository.findByTagsContaining(requiredTag, page);
        postResponse.setCount(postsPage.getTotalElements());
        List<Post> postsList = postsPage.getContent();
        postResponse.setPosts(fillAndGetArrayWithPosts(postsList));

        return postResponse;
    }

    public PostResponse getPostsByDate(Integer offset,
                                       Integer limit, String stringDate) {
        PostResponse postResponse = new PostResponse();
        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd"); возможно нужна доп. проверка,что переданная дата в правильном формате
        Page<Post> postsPage =
                getPostsListWithRequiredDate(checkAndGetOffset(offset),
                checkAndGetLimit(limit), stringDate);
        postResponse.setCount(postsPage.getTotalElements());
        List<Post> postsList = postsPage.getContent();
        postResponse.setPosts(fillAndGetArrayWithPosts(postsList));

        return postResponse;
    }

    public PostResponse getPostsListByQuery(Integer offset,
                                            Integer limit, String query) {
        PostResponse postResponse = new PostResponse();
        Page<Post> postsPage =
                getPostsListWithRequiredQuery(checkAndGetOffset(offset),
                        checkAndGetLimit(limit), query);
        postResponse.setCount(postsPage.getTotalElements());
        List<Post> postsList = postsPage.getContent();
        postResponse.setPosts(fillAndGetArrayWithPosts(postsList));

        return postResponse;
    }

    public PostResponse getPostsList(Integer offset,
                                     Integer limit, String stringMode) {
        PostResponse postResponse = new PostResponse();
        Page<Post> postsPage =
                getPostsListWithRequiredMode(checkAndGetOffset(offset),
                        checkAndGetLimit(limit), checkAndGetMode(stringMode));
        postResponse.setCount(postsPage.getTotalPages());
        List<Post> postsList = postsPage.getContent();
        postResponse.setPosts(fillAndGetArrayWithPosts(postsList));

        return postResponse;
    }

    private List<PostDTO> fillAndGetArrayWithPosts(List<Post> postsList) {
        List<PostDTO> list = new ArrayList<>();
        for (Post post : postsList) {
            PostDTO postDTO = new PostDTO();
            postDTO.setPostId(post.getId());
            postDTO.setTimestamp(post.getTime().toEpochSecond(ZoneOffset.UTC));
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
            list.add(postDTO);
        }
        return list;
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

    private Page<Post> checkAndGetPostsList(Page<Post> bufferPostsList) {
        List<Post> list = new ArrayList<>();
        for (Post post : bufferPostsList) {
            if (post.isActive()
                    && post.getModerationStatus() == ModerationStatus.ACCEPTED
                    && post.getTime().isAfter(LocalDateTime.now())) {
                list.add(post);
            }
        }
        Page<Post> page = new PageImpl<>(list);
        return page;
    }

    private Page<Post> getPostsListWithRequiredDate(Integer offset,
                                                    Integer limit,
                                                    String stringDate) {
        Page<Post> bufferPostsPage;
        Pageable page = PageRequest.of(offset, limit);
        bufferPostsPage = postRepository.findByTimeEquals(stringDate, page);
        return checkAndGetPostsList(bufferPostsPage);
    }

    private Page<Post> getPostsListWithRequiredQuery(Integer offset,
                                                     Integer limit,
                                                     String query) {
        Page<Post> bufferPostPage;
        Pageable page = PageRequest.of(offset, limit, Sort.by("time").descending());
        bufferPostPage = postRepository.findByTextContaining(query, page);
        return checkAndGetPostsList(bufferPostPage);
    }

    private Page<Post> getPostsListWithRequiredMode(Integer offset,
                                                    Integer limit, Mode mode) {
        Page<Post> bufferPostsPage;
        Page<Post> postsPage;
        Pageable pageable = PageRequest.of(offset / limit, limit);
        if (mode == Mode.RECENT) {
            bufferPostsPage = postRepository.findAllAndOrderByTimeDesc(pageable);
            postsPage = checkAndGetPostsList(bufferPostsPage);
        } else if (mode == Mode.POPULAR) {
            bufferPostsPage = postRepository.findAllAndOrderByCommentariesSize(pageable);
            postsPage = checkAndGetPostsList(bufferPostsPage);
        } else if (mode == Mode.BEST) {
            bufferPostsPage = postRepository.findAllAndOrderByVotesCount(pageable);
            postsPage = checkAndGetPostsList(bufferPostsPage);
        } else { // EARLY
            bufferPostsPage = postRepository.findAllAndOrderByTimeAsc(pageable);
            postsPage = checkAndGetPostsList(bufferPostsPage);
        }

        return postsPage;
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
