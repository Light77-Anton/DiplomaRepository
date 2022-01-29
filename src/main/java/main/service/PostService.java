package main.service;
import main.api.response.MyPostResponse;
import main.api.response.PostByIdResponse;
import main.api.response.PostResponse;
import main.model.*;
import main.model.repositories.PostRepository;
import main.model.repositories.TagRepository;
import main.model.repositories.UserRepository;
import main.support.dto.*;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.domain.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.security.Principal;
import java.time.*;
import java.util.*;

@Service
@Configurable
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final SubmethodsForService submethodsForService;

    public PostService(PostRepository postRepository,
                       UserRepository userRepository,
                       TagRepository tagRepository,
                       SubmethodsForService submethodsForService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
        this.submethodsForService = submethodsForService;
    }

    public MyPostResponse getMyPost(Integer offset,
                                    Integer limit,
                                    String status,
                                    Principal principal) {

        main.model.User currentUser = userRepository.findByEmail
                (principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        MyPostResponse myPostResponse = new MyPostResponse();
        Page<Post> postsPage = submethodsForService
                .getPostsPageWithRequiredStatus(
                submethodsForService.checkAndGetOffset(offset),
                submethodsForService.checkAndGetLimit(limit),
                submethodsForService.checkAndGetPostStatus(status),
                        currentUser);
        if (postsPage.isEmpty()) {
            myPostResponse.setCount(0);
            myPostResponse.setPosts(new ArrayList<>());
        }
        myPostResponse.setCount(postsPage.getTotalPages());
        List<Post> postsList = postsPage.getContent();
        List<MyPostDTO> myPostsList = new ArrayList<>();
        for (Post post : postsList) {
            MyPostDTO myPostDTO = new MyPostDTO();
            myPostDTO.setPostId(post.getId());
            myPostDTO.setTimestamp(post.getTime().toEpochSecond(ZoneOffset.UTC));
            myPostDTO.setTitle(post.getTitle());
            myPostDTO.setAnnounce(submethodsForService.getAnnounce(post.getText()));
            myPostDTO.setLikeCount(submethodsForService.getLikesCount(post));
            myPostDTO.setDislikeCount(submethodsForService.getDislikesCount(post));
            myPostDTO.setCommentCount(post.getCommentaries().size());
            myPostDTO.setViewCount(post.getViewCount());
            UserDataDTO userDataDTO = new UserDataDTO();
            userDataDTO.setId(post.getUserId());
            userDataDTO.setName(post.getUser().getName());
            myPostDTO.setUserData(userDataDTO);
            myPostsList.add(myPostDTO);
        }
        myPostResponse.setPosts(myPostsList);

        return myPostResponse;
    }

    public PostByIdResponse getPostById(Integer id, User currentUser) {
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
        UserDataDTO userDataDTO = new UserDataDTO();
        userDataDTO.setId(post.get().getUserId());
        userDataDTO.setName(post.get().getUser().getName());
        postByIdDTO.setUserData(userDataDTO);
        postByIdDTO.setTitle(post.get().getTitle());
        postByIdDTO.setText(post.get().getText());
        postByIdDTO.setLikesCount(submethodsForService.getLikesCount(post.get()));
        postByIdDTO.setDislikeCount(submethodsForService.getDislikesCount(post.get()));
        postByIdDTO.setViewCount(post.get().getViewCount());
        List<CommentsDataDTO> commentsDataDTOList = new ArrayList<>();
        List<Comment> commentList = post.get().getCommentaries();
        for (Comment comment : commentList) {
            CommentsDataDTO commentsDataDTO = new CommentsDataDTO();
            commentsDataDTO.setId(comment.getId());
            commentsDataDTO.setTimestamp(comment.getTime().toEpochSecond(ZoneOffset.UTC));
            commentsDataDTO.setText(comment.getText());
            ExtendedUserDataDTO extendedUserDataDTO = new ExtendedUserDataDTO();
            Optional<User> user = userRepository.findById(comment.getUserId());
            extendedUserDataDTO.setId(user.get().getId());
            extendedUserDataDTO.setName(user.get().getName());
            extendedUserDataDTO.setPhoto(user.get().getPhoto());
            commentsDataDTO.setUserData(extendedUserDataDTO);
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

        /**
         * Пока явно неоптимальный способ подсчета просмотров,
         * потом нужно пересмотреть,как можно использовать Principal
         */

        if (currentUser == null) {
            int postId = post.get().getId();
            int newViewCount = post.get().getViewCount() + 1;
            int rowsUpdated = postRepository.setNewViewCount(newViewCount, postId);
        }
        else {
            if (!currentUser.isModerator() || post.get().getUserId() != currentUser.getId()) {
                int postId = post.get().getId();
                int newViewCount = post.get().getViewCount() + 1;
                int rowsUpdated = postRepository.setNewViewCount(newViewCount, postId);
            }
        }

        return postByIdResponse;
    }

    public PostResponse getPostsByTag(Integer offset,
                                      Integer limit, String stringTag) {
        Optional<Tag> requiredTag = tagRepository.findByNameContaining(stringTag);
        PostResponse postResponse = new PostResponse();
        if (requiredTag.isEmpty()) {
            postResponse.setCount(0);
            postResponse.setPosts(new ArrayList<>());
            return postResponse;
        }
        Pageable page = PageRequest.of(
                submethodsForService.checkAndGetOffset(offset),
                submethodsForService.checkAndGetLimit(limit));
        Page<Post> postsPage = postRepository.findByTagContaining(requiredTag.get(), page);
        postResponse.setCount(postsPage.getTotalPages());
        List<Post> postsList = postsPage.getContent();
        postResponse.setPosts(submethodsForService.fillAndGetArrayWithPosts(postsList));

        return postResponse;
    }

    public PostResponse getPostsByDate(Integer offset,
                                       Integer limit, String stringDate) {
        PostResponse postResponse = new PostResponse();
        Page<Post> postsPage =
                submethodsForService.getPostsListWithRequiredDate(
                        submethodsForService.checkAndGetOffset(offset),
                        submethodsForService.checkAndGetLimit(limit),
                        stringDate);
        postResponse.setCount(postsPage.getTotalPages());
        List<Post> postsList = postsPage.getContent();
        postResponse.setPosts(submethodsForService.fillAndGetArrayWithPosts(postsList));

        return postResponse;
    }

    public PostResponse getPostsListByQuery(Integer offset,
                                            Integer limit, String query) {
        PostResponse postResponse = new PostResponse();
        Page<Post> postsPage =
                submethodsForService.getPostsListWithRequiredQuery(
                        submethodsForService.checkAndGetOffset(offset),
                        submethodsForService.checkAndGetLimit(limit),
                        query);
        postResponse.setCount(postsPage.getTotalPages());
        List<Post> postsList = postsPage.getContent();
        postResponse.setPosts(submethodsForService.fillAndGetArrayWithPosts(postsList));

        return postResponse;
    }

    public PostResponse getPostsList(Integer offset,
                                     Integer limit, String stringMode) {
        PostResponse postResponse = new PostResponse();
        Page<Post> postsPage =
                submethodsForService.getPostsPageWithRequiredMode(
                        submethodsForService.checkAndGetOffset(offset),
                        submethodsForService.checkAndGetLimit(limit),
                        submethodsForService.checkAndGetMode(stringMode));
        postResponse.setCount(postsPage.getTotalPages());
        List<Post> postsList = postsPage.getContent();
        postResponse.setPosts(submethodsForService.fillAndGetArrayWithPosts(postsList));

        return postResponse;
    }

}
