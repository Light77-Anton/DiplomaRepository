package main.service;
import main.api.request.PostModerateRequest;
import main.api.request.PostRequest;
import main.api.request.VoteForPostRequest;
import main.api.response.*;
import main.dto.*;
import main.model.*;
import main.model.repositories.*;
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
    private final CommentRepository commentRepository;
    private final SubmethodsForService submethodsForService;
    private final VoteRepository voteRepository;
    private final TagToPostRepository tagToPostRepository;
    private final SettingsService settingsService;

    public PostService(PostRepository postRepository,
                       UserRepository userRepository,
                       TagRepository tagRepository,
                       CommentRepository commentRepository,
                       SubmethodsForService submethodsForService,
                       VoteRepository voteRepository,
                       TagToPostRepository tagToPostRepository,
                       SettingsService settingsService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
        this.commentRepository = commentRepository;
        this.submethodsForService = submethodsForService;
        this.voteRepository = voteRepository;
        this.tagToPostRepository = tagToPostRepository;
        this.settingsService = settingsService;
    }

    public ResultResponse setLikeForPost(Principal principal,
                                         VoteForPostRequest voteForPostRequest) {
        ResultResponse resultResponse = new ResultResponse();
        main.model.User currentUser = userRepository.findByEmail
                        (principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        int likeValue = 1;
        int dislikeValue = 0;
        Optional<Post> post = postRepository.findById(voteForPostRequest.getPostId());
        if (post.isPresent()) {
            Optional<Vote> vote = voteRepository.findByUserAndPostId(currentUser.getId(), post.get().getId());
            if (vote.isEmpty()) {
                Vote newVote = new Vote();
                newVote.setUser(currentUser);
                newVote.setPost(post.get());
                newVote.setTime(LocalDateTime.now());
                newVote.setValue(likeValue);
                voteRepository.save(newVote);
                //voteRepository.insertVote(currentUser.getId(), post.get().getId(), likeValue);
                resultResponse.setResult(true);
                return resultResponse;
            } else if (vote.isPresent() && vote.get().getValue() == dislikeValue) {
                voteRepository.changeVote(currentUser.getId(), post.get().getId(), likeValue);
                resultResponse.setResult(true);
                return resultResponse;
            } else {
                resultResponse.setResult(false);
                return resultResponse;
            }
        } else {
            resultResponse.setResult(false);
            return resultResponse;
        }
    }

    public ResultResponse setDislikeForPost(Principal principal,
                                         VoteForPostRequest voteForPostRequest) {
        ResultResponse resultResponse = new ResultResponse();
        main.model.User currentUser = userRepository.findByEmail
                        (principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        int dislikeValue = 0;
        int likeValue = 1;
        Optional<Post> post = postRepository.findById(voteForPostRequest.getPostId());
        if (post.isPresent()) {
            Optional<Vote> vote = voteRepository.findByUserAndPostId(currentUser.getId(), post.get().getId());
            if (vote.isEmpty()) {
                Vote newVote = new Vote();
                newVote.setUser(currentUser);
                newVote.setPost(post.get());
                newVote.setTime(LocalDateTime.now());
                newVote.setValue(dislikeValue);
                voteRepository.save(newVote);
                //voteRepository.insertVote(currentUser.getId(), post.get().getId(), dislikeValue);
                resultResponse.setResult(true);
                return resultResponse;
            } else if (vote.isPresent() && vote.get().getValue() == likeValue) {
                voteRepository.changeVote(currentUser.getId(), post.get().getId(), dislikeValue);
                resultResponse.setResult(true);
                return resultResponse;
            } else {
                resultResponse.setResult(false);
                return resultResponse;
            }
        } else {
            resultResponse.setResult(false);
            return resultResponse;
        }
    }

    public ResultResponse checkModeratorDecision(PostModerateRequest postModerateRequest, Principal principal) {
        ResultResponse resultResponse = new ResultResponse();
        Optional<Post> post = postRepository.findById(postModerateRequest.getPostId());
        if (post.isEmpty()) {
            resultResponse.setResult(false);
            return resultResponse;
        }
        if (!postModerateRequest.getDecision().equals("ACCEPTED") &&
                !postModerateRequest.getDecision().equals("DECLINED")) {
            resultResponse.setResult(false);
            return resultResponse;
        }
        main.model.User currentUser = userRepository.findByEmail
                        (principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        resultResponse.setResult(true);
        if (postModerateRequest.getDecision().equals("ACCEPTED")) {
            int updatedRow = postRepository.moderatePost("ACCEPTED", currentUser.getId(), postModerateRequest.getPostId());
        } else {
            int updatedRow = postRepository.moderatePost("DECLINED", currentUser.getId(), postModerateRequest.getPostId());
        }

        return resultResponse;
    }

    public PostResultResponse updatePost(Integer id, PostRequest postRequest, Principal principal) {
        main.model.User currentUser = userRepository.findByEmail
                        (principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        Optional<Post> post = postRepository.findByIdAndUserId(id,currentUser.getId());
        PostResultResponse postResultResponse = new PostResultResponse();
        List<String> description = new ArrayList<>();
        if (post.isPresent()) {
            boolean isActive = postRequest.isActive();
            LocalDateTime ldt = submethodsForService.checkLocalDateTimeForPost(postRequest.getTimestamp());
            String title = submethodsForService.checkTitleForPost(postRequest.getTitle());
            if (title.equals("")) {
                description.add("Заголовок не установлен");
            }
            List<Tag> tags = submethodsForService.checkTagsListForPost(postRequest.getTags());
            if (postRequest.getTags().size() != tags.size()) {
                description.add("Такого тэга(-ов) не существует");
            }
            String text = submethodsForService.checkTextForPost(postRequest.getText());
            if (text.equals("")) {
                description.add("Текст публикации слишком короткий");
            }
            if (description.isEmpty()) {
                description.add("Все верно,изменения поста приняты");
                postResultResponse.setDescription(description);
                postResultResponse.setResult(true);
                if (settingsService.isPremoderation()) {
                    postRepository.updatePost(post.get().getId(), isActive, ldt, title, text, ModerationStatus.NEW);
                } else {
                    postRepository.updatePost(post.get().getId(), isActive, ldt, title, text, ModerationStatus.ACCEPTED);
                }
                for (Tag tag : tags) {
                    if (tagToPostRepository.findByPostAndTagId(post.get().getId(), tag.getId()).isEmpty()) {
                        TagToPost tagToPost = new TagToPost();
                        tagToPost.setPostId(postRepository.findLastPostIdByUserId(currentUser.getId()));
                        tagToPost.setTagId(tag.getId());
                        //tagToPostRepository.save(tagToPost);
                        tagToPostRepository.insertTagToPost(postRepository.findLastPostIdByUserId(currentUser.getId()), tag.getId());
                    }
                }
                return postResultResponse;
            }
        }
        postResultResponse.setResult(false);

        return postResultResponse;
    }

    public PostResultResponse post(PostRequest postRequest, Principal principal) {
        main.model.User currentUser = userRepository.findByEmail
                        (principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        PostResultResponse postResultResponse = new PostResultResponse();
        List<String> description = new ArrayList<>();
        boolean isActive = postRequest.isActive();
        LocalDateTime ldt = submethodsForService.checkLocalDateTimeForPost(postRequest.getTimestamp());
        String title = submethodsForService.checkTitleForPost(postRequest.getTitle());
        if (title.equals("")) {
            description.add("Заголовок не установлен");
        }
        List<Tag> tags = submethodsForService.checkTagsListForPost(postRequest.getTags());
        if (postRequest.getTags().size() != tags.size()) {
            description.add("Такого тэга(-ов) не существует");
        }
        String text = submethodsForService.checkTextForPost(postRequest.getText());
        if (text.equals("")) {
            description.add("Текст публикации слишком короткий");
        }
        if (description.isEmpty() && settingsService.isPremoderation()) {
            description.add("Все верно,пост будет опубликован в указанное время");
            postResultResponse.setDescription(description);
            postResultResponse.setResult(true);
            Post newPost = new Post();
            newPost.setActive(isActive);
            newPost.setUser(currentUser);
            newPost.setModerationStatus(ModerationStatus.NEW);
            newPost.setTime(ldt);
            newPost.setTitle(title);
            newPost.setText(text);
            postRepository.save(newPost);
            //postRepository.insertPost(isActive, currentUser.getId(), ldt, title, text, "NEW");
            for (Tag tag : tags) {
                TagToPost tagToPost = new TagToPost();
                tagToPost.setPostId(postRepository.findLastPostIdByUserId(currentUser.getId()));
                tagToPost.setTagId(tag.getId());
                //tagToPostRepository.save(tagToPost);
                tagToPostRepository.insertTagToPost(postRepository.findLastPostIdByUserId(currentUser.getId()), tag.getId());
            }
            return postResultResponse;
        }
        if (description.isEmpty() && !settingsService.isPremoderation()) {
            description.add("Все верно,пост будет опубликован в указанное время");
            postResultResponse.setDescription(description);
            postResultResponse.setResult(true);
            Post newPost = new Post();
            newPost.setActive(isActive);
            newPost.setUser(currentUser);
            newPost.setModerationStatus(ModerationStatus.ACCEPTED);
            newPost.setTime(ldt);
            newPost.setTitle(title);
            newPost.setText(text);
            postRepository.save(newPost);
            //postRepository.insertPost(isActive, currentUser.getId(), ldt, title, text, "ACCEPTED");
            for (Tag tag : tags) {
                TagToPost tagToPost = new TagToPost();
                tagToPost.setPostId(postRepository.findLastPostIdByUserId(currentUser.getId()));
                tagToPost.setTagId(tag.getId());
                //tagToPostRepository.save(tagToPost);
                tagToPostRepository.insertTagToPost(postRepository.findLastPostIdByUserId(currentUser.getId()), tag.getId());
            }
            return postResultResponse;
        }
        postResultResponse.setResult(false);

        return postResultResponse;
    }

    public MyPostResponse getPostsForModeration(Integer offset,
                                                Integer limit,
                                                ModerationStatus status,
                                                Principal principal) {
        main.model.User currentUser = userRepository.findByEmail
                        (principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        Pageable page = PageRequest.of(offset /limit, limit);
        MyPostResponse myPostResponse = new MyPostResponse();
        if (status == ModerationStatus.NEW) {
            Page<Post> postsPage = postRepository.findAllNewPostsAsPage(page);
            myPostResponse.setCount(postsPage.getTotalPages());
            myPostResponse.setPosts(submethodsForService.fillAndGetMyPostsList(postsPage.getContent()));
        } else if (status == ModerationStatus.ACCEPTED) {
            Page<Post> postsPage = postRepository.findAllAcceptedPostsByMe(currentUser.getId(), page);
            myPostResponse.setCount(postsPage.getTotalPages());
            myPostResponse.setPosts(submethodsForService.fillAndGetMyPostsList(postsPage.getContent()));
        } else if (status == ModerationStatus.DECLINED) {
            Page<Post> postsPage = postRepository.findAllDeclinedPostsByMe(currentUser.getId(), page);
            myPostResponse.setCount(postsPage.getTotalPages());
            myPostResponse.setPosts(submethodsForService.fillAndGetMyPostsList(postsPage.getContent()));
        }

        return myPostResponse;
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
        myPostResponse.setPosts(submethodsForService.fillAndGetMyPostsList(postsList));

        return myPostResponse;
    }

    public PostByIdResponse getPostById(Integer id, User currentUser) { //
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
        userDataDTO.setId(post.get().getUser().getId());
        userDataDTO.setName(post.get().getUser().getName());
        postByIdDTO.setUserData(userDataDTO);
        postByIdDTO.setTitle(post.get().getTitle());
        postByIdDTO.setText(post.get().getText());
        postByIdDTO.setLikesCount(postRepository.findLikeCountById(post.get().getId()));
        postByIdDTO.setDislikeCount(postRepository.findDislikeCountById(post.get().getId()));
        postByIdDTO.setViewCount(post.get().getViewCount());
        List<CommentsDataDTO> commentsDataDTOList = new ArrayList<>();
        List<Comment> commentList = post.get().getCommentaries();
        for (Comment comment : commentList) {
            CommentsDataDTO commentsDataDTO = new CommentsDataDTO();
            commentsDataDTO.setId(comment.getId());
            commentsDataDTO.setTimestamp(comment.getTime().toEpochSecond(ZoneOffset.UTC));
            commentsDataDTO.setText(comment.getText());
            ExtendedUserDataDTO extendedUserDataDTO = new ExtendedUserDataDTO();
            Optional<User> user = userRepository.findById(comment.getUser().getId());
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

        if (currentUser == null) {
            int postId = post.get().getId();
            int newViewCount = post.get().getViewCount() + 1;
            int rowsUpdated = postRepository.setNewViewCount(newViewCount, postId);
        }
        else {
            if (!currentUser.isModerator() || post.get().getUser().getId() != currentUser.getId()) {
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
        Page<Post> postsPage = postRepository.findByTagContaining(requiredTag.get().getId(), page);
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
