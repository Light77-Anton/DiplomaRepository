package main.service;
import main.api.request.CommentRequest;
import main.api.response.CommentSuccessResponse;
import main.api.response.FalseResultErrorsResponse;
import main.dto.MyPostDTO;
import main.model.*;
import main.model.repositories.CommentRepository;
import main.model.repositories.PostRepository;
import main.model.repositories.TagRepository;
import main.model.repositories.UserRepository;
import main.dto.PostDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import java.security.Principal;
import java.time.LocalDateTime;
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

    public CommentSuccessResponse getSuccessCommentId(Principal principal) {
        main.model.User currentUser = userRepository.findByEmail
                        (principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        CommentSuccessResponse commentSuccessResponse = new CommentSuccessResponse();
        commentSuccessResponse.setId(currentUser.getId());

        return commentSuccessResponse;
    }

    public FalseResultErrorsResponse getFailedCommentWithErrors(List<String> errors) {
        FalseResultErrorsResponse commentFailedResponse = new FalseResultErrorsResponse();
        commentFailedResponse.setErrors(errors);

        return commentFailedResponse;
    }

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
            /*
            Comment comment = new Comment();
            comment.setTime(LocalDateTime.now());
            comment.setParentId(commentRequest.getParentId());
            comment.setUserId(currentUser.getId());
            comment.setPostId(commentRequest.getPostId());
            comment.setText(commentRequest.getText());
            commentRepository.save(comment);
            if (commentRequest.getParentId() == null) {
                commentRepository.insertComment(commentRequest.getPostId(), currentUser.getId(), commentRequest.getText());
            } else {
                commentRepository.insertQuoteToComment(commentRequest.getParentId(), commentRequest.getPostId(), currentUser.getId(), commentRequest.getText());
            }

             */
            Comment newComment = new Comment();
            newComment.setUser(currentUser);
            newComment.setPost(post.get());
            newComment.setParentId(commentRequest.getParentId());
            newComment.setTime(LocalDateTime.now());
            newComment.setText(commentRequest.getText());
            commentRepository.save(newComment);
        }

        return errors;
    }

    public Page<PostDTO> getPostsPageWithRequiredMode(Integer offset,
                                                   Integer limit, Mode mode) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        if (mode == Mode.RECENT) {
            return postRepository.findAllAndOrderByTimeDescTest(pageable);
        } else if (mode == Mode.POPULAR) {
            return postRepository.findAllAndOrderByCommentariesSizeTest(pageable);
        } else if (mode == Mode.BEST) {
            return postRepository.findAllAndOrderByVotesCountTest(pageable);
        } else { //EARLY
            return postRepository.findAllAndOrderByTimeAscTest(pageable);
        }
    }

    public Page<MyPostDTO> getPostsPageWithRequiredStatus(Integer offset,
                                                     Integer limit,
                                                     PostStatus postStatus,
                                                     User currentUser) { // получить все id постов
        Pageable pageable = PageRequest.of(offset / limit, limit);
        int userId = currentUser.getId();
        if (postStatus == PostStatus.INACTIVE) {
            return postRepository.findAllInactivePostsTest(userId, pageable);
        } else if (postStatus == PostStatus.PENDING) {
            return postRepository.findAllPendingPostsTest(userId, pageable);
        } else if (postStatus == PostStatus.DECLINED) {
            return postRepository.findAllDeclinedPostsTest(userId, pageable);
        } else  { // PUBLISHED
            return postRepository.findAllAcceptedPostsTest(userId, pageable);
        }
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

    public Page<PostDTO> getPostsListWithRequiredDate(Integer offset,
                                                   Integer limit,
                                                   String stringDate) { // получить все id постов
        Pageable page = PageRequest.of(offset / limit, limit);
        return postRepository.findByDateTest(stringDate, page);
    }

    public Page<PostDTO> getPostsListWithRequiredQuery(Integer offset,
                                                    Integer limit,
                                                    String query) { // получить все id постов
        Pageable page = PageRequest.of(offset / limit, limit, Sort.by("time").descending());
        return postRepository.findByTextContainingTest(query, page);
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

    public String checkTitleForPost(String titleFromPostRequest) {
        String title;
        if (titleFromPostRequest.length() <= 3) {
            title = "";
        } else {
            title = titleFromPostRequest;
        }

        return title;
    }

    public String checkTextForPost(String textFromPostRequest) {
        String text;
        if (textFromPostRequest.length() <= 50) {
            text = "";
        } else {
            text = textFromPostRequest;
        }

        return text;
    }

    public List<Tag> checkTagsListForPost(List<String> tagsFromPostRequest) {
        List<Tag> tags = new ArrayList<>();
        for (String tagName : tagsFromPostRequest) {
            Optional<Tag> tag = tagRepository.findByNameContaining(tagName);
            if (tag.isEmpty()) {
            } else {
                tags.add(tag.get());
            }
        }

        return tags;
    }
}
