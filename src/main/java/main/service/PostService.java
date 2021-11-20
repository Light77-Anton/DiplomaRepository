package main.service;
import main.api.response.PostByIdResponse;
import main.api.response.PostResponse;
import main.model.*;
import main.support.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
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

    private Integer checkedOffset;
    private Integer checkedLimit;


    public PostService() {

    }

    /*
    public PostResponse getPostsForModeration() {

        PostResponse postResponse = new PostResponse();
        if (!checkParamsWithModerationStatus(offset, limit, status)) {
            return postResponse;
        }
        postResponse.setCount(postRepository.count());
        Iterable<Post> posts = postRepository.findAll();
        for (Post post : posts) {
            if () {

            }
        }

    }
     */

    public PostByIdResponse getPostById(Integer id) {
        PostByIdResponse postByIdResponse = null;
        Optional<Post> post = postRepository.findById(id);
        if (!post.isPresent()) {
            return postByIdResponse;
        }
        postByIdResponse = new PostByIdResponse();
        JSONObject obj = new JSONObject();
        obj.put("id", post.get().getId());
        obj.put("timestamp", post.get().getTime().getTime());
        obj.put("active", post.get().isActive());
        JSONObject userObj = new JSONObject();
        userObj.put("id", post.get().getUserId());
        userObj.put("name", post.get().getUser().getName());
        obj.put("user", userObj);
        obj.put("title", post.get().getTitle());
        obj.put("text", post.get().getText());
        obj.put("likesCount", getLikesCount(post.get()));
        obj.put("dislikesCount", getDislikesCount(post.get()));
        obj.put("viewsCount", post.get().getViewCount());
        JSONArray commentsArray = new JSONArray();
        List<Comment> commentList = post.get().getCommentaries();
        for (Comment comment : commentList) {
            JSONObject commentObj = new JSONObject();
            commentObj.put("id", comment.getId());
            commentObj.put("timestamp", comment.getTime().getTime());
            commentObj.put("text", comment.getText());
            JSONObject commentUserObj = new JSONObject();
            commentUserObj.put("id", comment.getUser().getId());
            commentUserObj.put("name", comment.getUser().getName());
            commentUserObj.put("photo", comment.getUser().getPhoto());
            commentObj.put("user", commentUserObj);
        }
        obj.put("comments", commentsArray);
        JSONArray tagsArray = new JSONArray();
        List<Tag> tagsList = post.get().getTags();
        for (Tag tag : tagsList) {
            tagsArray.put(tag.getName());
        }
        obj.put("tags", tagsArray);
        postByIdResponse.setPostData(obj);

        return postByIdResponse;
    }

    public PostResponse getPostsByTag(Integer offset,Integer limit, String stringTag) {
        checkParams(offset, limit);
        Tag requiredTag = checkAndGetRequiredTag(stringTag);
        PostResponse postResponse = new PostResponse();
        if (requiredTag == null) {
            postResponse.setCount(0);
            postResponse.setPosts(new JSONArray());
            return postResponse;
        }
        List<Post> listWithPosts = requiredTag.getPosts();
        postResponse.setCount(listWithPosts.size());
        postResponse.setPosts(fillAndGetArrayWithPosts(checkedOffset, checkedLimit, listWithPosts));

        return postResponse;
    }

    public PostResponse getPostsByDate(Integer offset, Integer limit, String stringDate) {
        checkParams(offset, limit);
        PostResponse postResponse = new PostResponse();
        Iterable<Post> posts = postRepository.findAll();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<Post> listWithPosts = new ArrayList<>();
        int postsCountForRequiredDate = 0;
        for (Post post : posts) {
            String date = simpleDateFormat.format(post.getTime());
            if (date.equals(stringDate)) {
                listWithPosts.add(post);
                postsCountForRequiredDate++;
            }
        }
        postResponse.setCount(postsCountForRequiredDate);
        postResponse.setPosts(fillAndGetArrayWithPosts(checkedOffset, checkedLimit, listWithPosts));

        return postResponse;
    }

    public PostResponse getPostsListByQuery(Integer offset, Integer limit, String query) {
        checkParams(offset, limit);
        PostResponse postResponse = new PostResponse();
        Iterable<Post> posts = postRepository.findAll();
        List<Post> bufferPostsList = new ArrayList<>();
        for (Post post : posts) {
            if (post.getText().contains(query) && post.isActive()
                    && post.getModerationStatus() == ModerationStatus.ACCEPTED
                    && post.getTime().getTime() <= System.currentTimeMillis()) {
                bufferPostsList.add(post);
            }
        }
        postResponse.setCount(bufferPostsList.size());
        List<Post> postsList = sortListByMode(checkAndGetMode(query), bufferPostsList);
        /**
         * передаем query на место mode т.к. если метод не находит совпадения в Enum values() - вернет Mode.RECENT
         */
        postResponse.setPosts(fillAndGetArrayWithPosts(checkedOffset, checkedLimit, postsList));

        return postResponse;
    }

    public PostResponse getPostsList(Integer offset, Integer limit, String stringMode) {
        checkParams(offset, limit);
        PostResponse postResponse = new PostResponse();
        postResponse.setCount(postRepository.count());
        Iterable<Post> posts = postRepository.findAll();
        List<Post> bufferPostsList = new ArrayList<>();
        for (Post post : posts) {
            bufferPostsList.add(post);
        }
        List<Post> postsList = sortListByMode(checkAndGetMode(stringMode), bufferPostsList);
        postResponse.setPosts(fillAndGetArrayWithPosts(checkedOffset, checkedLimit, postsList));

        return postResponse;
    }

    private Tag checkAndGetRequiredTag(String stringTag) {
        Iterable<Tag> tags = tagRepository.findAll();
        for (Tag tag : tags) {
            if (tag.getName().equals(stringTag)) {
                return tag;
            }
        }

        return null;
    }

    /*
    private boolean checkParamsWithModerationStatus (Integer offset, Integer limit, String stringStatus) {
        if (offset == null) {
            offset = 0;
            this.offset = offset;
        }
        if (limit == null) {
            limit = 10;
            this.limit = limit;
        }
        for (ModerationStatus statusValue : ModerationStatus.values()) {
            if (statusValue.name().equals(status.name())) {
                return true;
            }
        }

        return false;
    }

     */

    private JSONArray fillAndGetArrayWithPosts(Integer offset, Integer limit, List<Post> postsList) {
        JSONArray array = new JSONArray();
        for (Post post : postsList) {
            if (array.length() >= limit) {
                break;
            } else if (offset != 0) {
                offset--;
            } else {
                JSONObject obj = new JSONObject();
                obj.put("id", post.getId());
                obj.put("timestamp", post.getTime().getTime());
                JSONObject userObj = new JSONObject();
                userObj.put("id", post.getUserId());
                userObj.put("name", post.getUser().getName());
                obj.put("user", userObj);
                obj.put("title", post.getTitle());
                obj.put("announce", getAnnounce(post.getText()));
                obj.put("likesCount", getLikesCount(post));
                obj.put("dislikesCount", getDislikesCount(post));
                obj.put("commentCount", post.getCommentaries().size());
                obj.put("viewsCount", post.getViewCount());
                array.put(obj);
            }
        }

        return array;
    }

    private Mode checkAndGetMode(String stringMode) {
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

    private void checkParams(Integer offset, Integer limit) {
        if (offset == null || offset < 0) {
            offset = 0;
            this.checkedOffset = offset;
        }
        if (limit == null || limit < 0) {
            limit = 10;
            this.checkedLimit = limit;
        }
    }

    private List<Post> sortListByMode(Mode mode, List<Post> list) {

        if (mode == Mode.RECENT) {
            list.sort(new TimestampComparator());
        } else if (mode == Mode.POPULAR) {
            list.sort(new CommentsComparator());
        } else if (mode == Mode.BEST) {
            list.sort(new VotesComparator());
        } else if (mode == Mode.EARLY) {
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
