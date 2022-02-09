package main.service;
import main.api.request.CommentRequest;
import main.dto.MyPostDTO;
import main.model.*;
import main.model.repositories.CommentRepository;
import main.model.repositories.PostRepository;
import main.model.repositories.TagRepository;
import main.model.repositories.UserRepository;
import main.dto.CountForPostId;
import main.dto.PostDTO;
import main.dto.UserDataDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class SubmethodsForService {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private CommentRepository commentRepository;

    /**
     * Добавил дополнительный класс для PostService т.к. сам класс сервис стал неимоверно
     * большим.Здесь будут все вспомогательные методы.
     */

    public List<String> checkAndAddComment(CommentRequest commentRequest, Principal principal) {
        Optional<Post> post = postRepository.findById(commentRequest.getPostId());
        List<String> errors = new ArrayList<>();
        if (post.isEmpty()) {
            errors.add("Такого поста не существует");
        } else {
            if (commentRequest.getParentId() != null) {
                Optional<Comment> parentComment = commentRepository.findById(commentRequest.getParentId());
                if (parentComment.isEmpty()) {
                    errors.add("Такого комментария не существует");
                }
            }
        }
        if (commentRequest.getText().length() <= 5) {
            errors.add("Комментарий слишком короткий");
        }
        if (errors.isEmpty()) {
            main.model.User currentUser = userRepository.findByEmail
                            (principal.getName())
                    .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
            commentRepository.insertComment(commentRequest.getParentId(), commentRequest.getPostId(), currentUser.getId(), commentRequest.getText());
        }

        return errors;
    }

    public List<PostDTO> fillAndGetArrayWithPosts(List<Post> postsList) {
        List<PostDTO> list = new ArrayList<>();
        for (Post post : postsList) {
            PostDTO postDTO = new PostDTO();
            postDTO.setPostId(post.getId());
            postDTO.setTimestamp(post.getTime().toEpochSecond(ZoneOffset.UTC));
            UserDataDTO userDataDTO = new UserDataDTO();
            userDataDTO.setId(post.getUserId());
            userDataDTO.setName(post.getUser().getName());
            postDTO.setUserData(userDataDTO);
            postDTO.setTitle(post.getTitle());
            postDTO.setAnnounce(postRepository.extractAnnounceFromTextById(post.getId()) + "...");
            postDTO.setLikesCount(postRepository.findLikeCountById(post.getId()));
            postDTO.setDislikeCount(postRepository.findDislikeCountById(post.getId()));
            postDTO.setCommentCount(post.getCommentaries().size());
            postDTO.setViewCount(postRepository.findViewCountById(post.getId()));
            list.add(postDTO);
        }
        return list;
    }

    public Page<Post> getPostsPageWithRequiredMode(Integer offset,
                                                   Integer limit, Mode mode) {
        Page<CountForPostId> bufferArrayList;
        List<Post> postsList;
        Page<Post> postsPage;
        Pageable pageable = PageRequest.of(offset / limit, limit);
        if (mode == Mode.RECENT) {
            postsPage = postRepository.findAllAndOrderByTimeDesc(pageable);
        } else if (mode == Mode.POPULAR) {
            bufferArrayList = postRepository.findAllAndOrderByCommentariesSize(pageable);//
            postsList = new ArrayList<>();
            for (CountForPostId array : bufferArrayList) {
                Integer postId = Integer.valueOf(String.valueOf(array.getId()));
                if (postId == null) {
                    break;
                }
                Optional<Post> post = postRepository.findById(postId);
                postsList.add(post.get());
            }
            postsPage = new PageImpl<>(postsList);
        } else if (mode == Mode.BEST) {
            bufferArrayList = postRepository.findAllAndOrderByVotesCount(pageable);//
            postsList = new ArrayList<>();
            for (CountForPostId array : bufferArrayList) {
                Integer postId = Integer.valueOf(String.valueOf(array.getId()));
                if (postId == null) {
                    break;
                }
                Optional<Post> post = postRepository.findById(postId);
                postsList.add(post.get());
            }
            postsPage = new PageImpl<>(postsList);
        } else { //EARLY
            postsPage = postRepository.findAllAndOrderByTimeAsc(pageable);
        }

        return postsPage;
    }

    public Page<Post> getPostsPageWithRequiredStatus(Integer offset,
                                                     Integer limit,
                                                     PostStatus postStatus,
                                                     User currentUser) {
        Page<Post> postsPage = null;
        Pageable pageable = PageRequest.of(offset / limit, limit);
        int userId = currentUser.getId();
        if (postStatus == PostStatus.INACTIVE) {
            postsPage = postRepository.findAllInactivePosts(userId, pageable);
        } else if (postStatus == PostStatus.PENDING) {
            postsPage = postRepository.findAllPendingPosts(userId, pageable);
        } else if (postStatus == PostStatus.DECLINED) {
            postsPage = postRepository.findAllDeclinedPosts(userId, pageable);
        } else if (postStatus == PostStatus.PUBLISHED) {
            postsPage = postRepository.findAllAcceptedPosts(userId, pageable);
        }

        return postsPage;
    }

    public List<MyPostDTO> fillAndGetMyPostsList(List<Post> postsList) {
        List<MyPostDTO> myPostsList = new ArrayList<>();
        for (Post post : postsList) {
            MyPostDTO myPostDTO = new MyPostDTO();
            myPostDTO.setPostId(post.getId());
            myPostDTO.setTimestamp(post.getTime().toEpochSecond(ZoneOffset.UTC));
            myPostDTO.setTitle(post.getTitle());
            myPostDTO.setAnnounce(postRepository.extractAnnounceFromTextById(post.getId()) + "...");
            myPostDTO.setLikeCount(postRepository.findLikeCountById(post.getId()));
            myPostDTO.setDislikeCount(postRepository.findDislikeCountById(post.getId()));
            myPostDTO.setCommentCount(post.getCommentaries().size());
            myPostDTO.setViewCount(postRepository.findViewCountById(post.getId()));
            UserDataDTO userDataDTO = new UserDataDTO();
            userDataDTO.setId(post.getUserId());
            userDataDTO.setName(post.getUser().getName());
            myPostDTO.setUserData(userDataDTO);
            myPostsList.add(myPostDTO);
        }

        return myPostsList;
    }

    public PostStatus checkAndGetPostStatus(String stringStatus) {
        PostStatus postStatus = null;
        for (PostStatus statusValue : PostStatus.values()) {
            if (statusValue.name().equals(stringStatus)) {
                postStatus = statusValue;
            }
        }

        return postStatus;
    }

    public Page<Post> getPostsListWithRequiredDate(Integer offset,
                                                   Integer limit,
                                                   String stringDate) {
        Pageable page = PageRequest.of(offset / limit, limit);
        return postRepository.findByDate(stringDate, page);
    }

    public Page<Post> getPostsListWithRequiredQuery(Integer offset,
                                                    Integer limit,
                                                    String query) {
        Pageable page = PageRequest.of(offset / limit, limit, Sort.by("time").descending());
        return postRepository.findByTextContaining(query, page);
    }

    public Mode checkAndGetMode(String stringMode) {
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

    public Integer checkAndGetOffset(Integer offset) {
        if (offset == null || offset < 0) {
            offset = 0;
        }

        return offset;
    }

    public Integer checkAndGetLimit(Integer limit) {
        if (limit == null || limit < 0) {
            limit = 10;
        }

        return limit;
    }

    public LocalDateTime checkLocalDateTimeForPost(LocalDateTime fromPostRequestTime) {
        LocalDateTime ldt;
        if (fromPostRequestTime.isBefore(LocalDateTime.now())) {
            ldt = LocalDateTime.now();
        } else {
            ldt = fromPostRequestTime;
        }

        return ldt;
    }

    public String checkTitleForPost(String titleFromPostRequest, List<String> description) {
        String title;
        if (titleFromPostRequest.length() <= 3) {
            description.add("Заголовок не установлен");
            title = "";
        } else {
            title = titleFromPostRequest;
        }

        return title;
    }

    public String checkTextForPost(String textFromPostRequest, List<String> description) {
        String text;
        if (textFromPostRequest.length() <= 50) {
            description.add("Текст публикации слишком короткий");
            text = "";
        } else {
            text = textFromPostRequest;
        }

        return text;
    }

    public List<Tag> checkTagsListForPost(List<String> tagsFromPostRequest, List<String> description) {
        List<Tag> tags = new ArrayList<>();
        for (String tagName : tagsFromPostRequest) {
            Optional<Tag> tag = tagRepository.findByNameContaining(tagName);
            if (tag.isEmpty()) {
                description.add("Такого тэга(-ов) не существует");
            } else {
                tags.add(tag.get());
            }
        }

        return tags;
    }
}
