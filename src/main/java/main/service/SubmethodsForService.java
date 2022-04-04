package main.service;
import main.api.request.CommentRequest;
import main.api.request.PostRequest;
import main.api.response.CommentSuccessResponse;
import main.api.response.FalseResultErrorsResponse;
import main.dto.MyPostDTO;
import main.dto.UserDataDTO;
import main.model.*;
import main.model.repositories.*;
import main.dto.PostDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

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
    @Autowired
    private VoteRepository voteRepository;

    public List<String> checkDataForPost(String title, String text, List<Tag> tags, PostRequest postRequest) {
        List<String> description = new ArrayList<>();
        if (title.equals("")) {
            description.add("Заголовок не установлен");
        }
        if (postRequest.getTags() != null) {
            if (postRequest.getTags().size() != tags.size()) {
                description.add("Такого тэга(-ов) не существует");
            }
        }
        if (text.equals("")) {
            description.add("Текст публикации слишком короткий");
        }

        return description;
    }

    public Optional<Post> getOptionalPostByIdAndUserId(Integer postId, Principal principal) {
        main.model.User currentUser = userRepository.findByEmail
                        (principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));

        return postRepository.findByIdAndUserId(postId,currentUser.getId());
    }

    public List<PostDTO> fillAndGetArrayWithPosts(List<Post> postsList) {
        List<PostDTO> list = new ArrayList<>();
        for (Post post : postsList) {
            PostDTO postDTO = new PostDTO();
            postDTO.setPostId(post.getId());
            postDTO.setTimestamp(post.getTime().toEpochSecond(ZoneOffset.UTC));
            UserDataDTO userDataDTO = new UserDataDTO();
            userDataDTO.setId(post.getUser().getId());
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
        List<String> errors = new ArrayList<>();
        if (commentRequest.getPostId() == null) {
            errors.add("Не указан пост");
            return errors;
        }
        if (commentRequest.getText() == null) {
            errors.add("Комментарий пуст");
            return errors;
        }
        Optional<Post> post = postRepository.findById(commentRequest.getPostId());
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
            userDataDTO.setId(post.getUser().getId());
            userDataDTO.setName(post.getUser().getName());
            myPostDTO.setUserData(userDataDTO);
            myPostsList.add(myPostDTO);
        }

        return myPostsList;
    }

    public Pageable getCheckedPageable(Integer offset, Integer limit) {
        Pageable pageable;
        if (offset == null || offset < 0) {
            offset = 0;
        }
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        pageable = PageRequest.of(offset / limit, limit);

        return pageable;
    }

    public LocalDateTime checkLocalDateTimeForPost(LocalDateTime fromPostRequestTime) {
        LocalDateTime ldt;
        if (fromPostRequestTime == null) {
            ldt = LocalDateTime.now();
        } else if (fromPostRequestTime.isBefore(LocalDateTime.now())) {
            ldt = LocalDateTime.now();
        } else {
            ldt = fromPostRequestTime;
        }

        return ldt;
    }

    public String checkTitleForPost(String titleFromPostRequest) {
        String title;
        if (titleFromPostRequest == null) {
            title = "";
        } else if (titleFromPostRequest.length() <= 3) {
            title = "";
        } else {
            title = titleFromPostRequest;
        }

        return title;
    }

    public String checkTextForPost(String textFromPostRequest) {
        String text;
        if (textFromPostRequest == null) {
            text = "";
        } else if (textFromPostRequest.length() <= 50) {
            text = "";
        } else {
            text = textFromPostRequest;
        }

        return text;
    }

    public List<Tag> checkTagsListForPost(List<String> tagsFromPostRequest) {
        List<Tag> tags = new ArrayList<>();
        if (tagsFromPostRequest == null) {
            return tags;
        }
        for (String tagName : tagsFromPostRequest) {
            Optional<Tag> tag = tagRepository.findByNameContaining(tagName);
            if (tag.isPresent()) {
                tags.add(tag.get());
            }
        }

        return tags;
    }
}
