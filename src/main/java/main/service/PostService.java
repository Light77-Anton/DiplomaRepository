package main.service;
import main.api.request.PostModerateRequest;
import main.api.request.PostRequest;
import main.api.request.VoteForPostRequest;
import main.api.response.*;
import main.dto.*;
import main.model.*;
import main.model.repositories.*;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.domain.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.*;
import java.util.*;
import java.util.List;

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
    private static final long UPLOAD_LIMIT = 5242880;

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

    public String uploadImageAndGetLink(MultipartFile image) {
        if (image.getSize() > UPLOAD_LIMIT) {
            return "???????????? ?????????????????????? ???????????? ???????? ???? ?????????? 5 ????";
        }
        if (!image.getOriginalFilename().endsWith("jpg") && !image.getOriginalFilename().endsWith("png")) {
            return "???????????????????????? ???????????? ??????????????????????";
        }
        try {
            BufferedImage bufferedImage = ImageIO.read(image.getInputStream());
            char[] availableChars = "abcdefghijklmnopqrstuvwxyz0123456789"
                    .toCharArray();
            StringBuilder firstSubfolder = new StringBuilder();
            StringBuilder secondSubfolder = new StringBuilder();
            StringBuilder thirdSubfolder = new StringBuilder();
            StringBuilder newImageName = new StringBuilder();
            Random random = new Random();
            for (int i = 0; i < 5; i++) {
                if (i <= 1) {
                    firstSubfolder.append(availableChars[random.nextInt(availableChars
                            .length)]);
                    secondSubfolder.append(availableChars[random.nextInt(availableChars
                            .length)]);
                    thirdSubfolder.append(availableChars[random.nextInt(availableChars
                            .length)]);
                }
                newImageName.append(availableChars[random.nextInt(availableChars
                        .length)]);
            }
            String extension = FilenameUtils.getExtension(image.getOriginalFilename());
            String pathToImage = "upload/"
                    + firstSubfolder + "/" + secondSubfolder + "/" + thirdSubfolder + "/" + newImageName + "." + extension;
            Path path = Paths.get(pathToImage);
            File pathFile = path.toFile();
            pathFile.mkdirs();
            ImageIO.write(bufferedImage, extension, pathFile);
            return pathToImage;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public ResultResponse setVoteForPost(Principal principal,
                                         VoteForPostRequest voteForPostRequest,
                                         int value) {
        ResultResponse resultResponse = new ResultResponse();
        if (voteForPostRequest.getPostId() == null) {
            return resultResponse;
        }
        main.model.User currentUser = userRepository.findByEmail
                        (principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        int oppositeValue;
        if (value == 1) {
            oppositeValue = 0;
        } else {
            oppositeValue = 1;
        }
        Optional<Post> post = postRepository.findById(voteForPostRequest.getPostId());
        if (post.isPresent()) {
            Optional<Vote> vote = voteRepository.findByUserAndPostId(currentUser.getId(), post.get().getId());
            if (vote.isEmpty()) {
                Vote newVote = new Vote();
                newVote.setUser(currentUser);
                newVote.setPost(post.get());
                newVote.setTime(LocalDateTime.now());
                newVote.setValue(value);
                voteRepository.save(newVote);
                resultResponse.setResult(true);
                return resultResponse;
            } else if (vote.isPresent() && vote.get().getValue() == oppositeValue) {
                voteRepository.changeVote(currentUser.getId(), post.get().getId(), value);
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
        if (postModerateRequest.getPostId() == null || postModerateRequest.getDecision() == null) {
            return resultResponse;
        }
        Optional<Post> post = postRepository.findById(postModerateRequest.getPostId());
        if (post.isEmpty()) {
            resultResponse.setResult(false);
            return resultResponse;
        }
        main.model.User currentUser = userRepository.findByEmail
                        (principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        switch (postModerateRequest.getDecision()) {
            case "ACCEPTED":
                postRepository.moderatePost("ACCEPTED", currentUser.getId(), postModerateRequest.getPostId());
                resultResponse.setResult(true);
                break;
            case "DECLINED":
                postRepository.moderatePost("DECLINED", currentUser.getId(), postModerateRequest.getPostId());
                resultResponse.setResult(true);
                break;
            default:
                resultResponse.setResult(false);
        }

        return resultResponse;
    }

    public ResultDescriptionResponse updatePost(Integer id, PostRequest postRequest, Principal principal) {
        main.model.User currentUser = userRepository.findByEmail
                        (principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        Post post = postRepository.findByIdAndUserId(id,currentUser.getId()).get();
        ResultDescriptionResponse result = new ResultDescriptionResponse();
        List<String> description;
            boolean isActive = postRequest.isActive();
            LocalDateTime ldt = submethodsForService.checkLocalDateTimeForPost(postRequest.getTimestamp());// ?? ?????????????? "YYYY-MM-DDT12:00:00.000000000"
            String title = submethodsForService.checkTitleForPost(postRequest.getTitle());
            String text = submethodsForService.checkTextForPost(postRequest.getText());
            List<Tag> tags = submethodsForService.checkTagsListForPost(postRequest.getTags());
            description = submethodsForService.checkDataForPost(title, text, tags, postRequest);
            if (description.isEmpty()) {
                description.add("?????? ??????????,?????????????????? ?????????? ??????????????");
                result.setDescription(description);
                result.setResult(true);
                if (settingsService.isPremoderation()) {
                    post.setActive(isActive);
                    post.setTime(ldt);
                    post.setTitle(title);
                    post.setText(text);
                    post.setModerationStatus("NEW");
                    postRepository.save(post);
                } else {
                    post.setActive(isActive);
                    post.setTime(ldt);
                    post.setTitle(title);
                    post.setText(text);
                    post.setModerationStatus("ACCEPTED");
                    postRepository.save(post);
                }
                tagToPostRepository.deleteAll(tagToPostRepository.findAllByPostId(post.getId()));
                for (Tag tag : tags) {
                    tagToPostRepository.insertTagToPost(post.getId(), tag.getId());
                }
                return result;
            }

        result.setDescription(description);

        return result;
    }

    public ResultDescriptionResponse post(PostRequest postRequest, Principal principal) {
        main.model.User currentUser = userRepository.findByEmail
                        (principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        ResultDescriptionResponse response = new ResultDescriptionResponse();
        List<String> description;
        boolean isActive = postRequest.isActive();
        LocalDateTime ldt = submethodsForService.checkLocalDateTimeForPost(postRequest.getTimestamp()); // ?? ?????????????? "YYYY-MM-DDT12:00:00.000000000"
        String title = submethodsForService.checkTitleForPost(postRequest.getTitle());
        String text = submethodsForService.checkTextForPost(postRequest.getText());
        List<Tag> tags = submethodsForService.checkTagsListForPost(postRequest.getTags());
        description = submethodsForService.checkDataForPost(title, text, tags, postRequest);
        if (description.isEmpty()) {
            description.add("?????? ??????????,???????? ?????????? ?????????????????????? ?? ?????????????????? ??????????");
            response.setDescription(description);
            response.setResult(true);
            Post newPost = new Post();
            newPost.setActive(isActive);
            newPost.setUser(currentUser);
            if (currentUser.isModerator()) {
                newPost.setModeratorId(currentUser.getId());
            }
            newPost.setTime(ldt);
            newPost.setTitle(title);
            newPost.setText(text);
            if (settingsService.isPremoderation()) {
                newPost.setModerationStatus("NEW");
            } else {
                newPost.setModerationStatus("ACCEPTED");
            }
            postRepository.save(newPost);
            for (Tag tag : tags) {
                tagToPostRepository.insertTagToPost(postRepository.findLastPostIdByUserId(currentUser.getId()), tag.getId());
            }
            return response;
        }
        response.setDescription(description);

        return response;
    }

    public MyPostResponse getPostsForModeration(Integer offset,
                                                Integer limit,
                                                String status,
                                                Principal principal) {
        main.model.User currentUser = userRepository.findByEmail
                        (principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        MyPostResponse myPostResponse = new MyPostResponse();
        Pageable pageable = submethodsForService.getCheckedPageable(offset, limit);
        Page<Post> postsPage;
        switch (status) {
            case "NEW":
                postsPage = postRepository.findAllNewPostsAsPage(pageable);
                myPostResponse.setCount(postsPage.getTotalElements());
                myPostResponse.setPosts(submethodsForService.fillAndGetMyPostsList(postsPage.getContent()));
                break;
            case "ACCEPTED":
                postsPage = postRepository.findAllAcceptedPostsByMe(currentUser.getId(), pageable);
                myPostResponse.setCount(postsPage.getTotalElements());
                myPostResponse.setPosts(submethodsForService.fillAndGetMyPostsList(postsPage.getContent()));
                break;
            case "DECLINED":
                postsPage = postRepository.findAllDeclinedPostsByMe(currentUser.getId(), pageable);
                myPostResponse.setCount(postsPage.getTotalElements());
                myPostResponse.setPosts(submethodsForService.fillAndGetMyPostsList(postsPage.getContent()));
                break;
            default:
                myPostResponse.setCount(0);
                myPostResponse.setPosts(new ArrayList<>());
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
        Pageable pageable = submethodsForService.getCheckedPageable(offset, limit);
        Page<Post> postsPage;
        switch (status) {
            case "INACTIVE":
                postsPage = postRepository.findAllInactivePosts(currentUser.getId(), pageable);
                break;
            case "PENDING":
                postsPage = postRepository.findAllPendingPosts(currentUser.getId(), pageable);
                break;
            case "DECLINED":
                postsPage = postRepository.findAllDeclinedPosts(currentUser.getId(), pageable);
                break;
            case "ACCEPTED":
                postsPage = postRepository.findAllAcceptedPosts(currentUser.getId(), pageable);
                break;
            default:
                myPostResponse.setCount(0);
                myPostResponse.setPosts(new ArrayList<>());
                return myPostResponse;
        }
        myPostResponse.setCount(postsPage.getTotalElements());
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
        if (!post.get().isActive() || !post.get().getModerationStatus().equals("ACCEPTED") || !post.get().getTime().isBefore(LocalDateTime.now())) {
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
            postRepository.setNewViewCount(newViewCount, postId);
        }
        else {
            if (!currentUser.isModerator() && post.get().getUser().getId() != currentUser.getId()) {
                int postId = post.get().getId();
                int newViewCount = post.get().getViewCount() + 1;
                postRepository.setNewViewCount(newViewCount, postId);
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
        Pageable pageable = submethodsForService.getCheckedPageable(offset, limit);
        Page<Post> postsPage = postRepository.findByTagContaining(requiredTag.get().getId(), pageable);
        postResponse.setCount(postsPage.getTotalElements());
        List<Post> postsList = postsPage.getContent();
        postResponse.setPosts(submethodsForService.fillAndGetArrayWithPosts(postsList));

        return postResponse;
    }

    public PostResponse getPostsByDate(Integer offset,
                                       Integer limit, String stringDate) {
        PostResponse postResponse = new PostResponse();
        Pageable pageable = submethodsForService.getCheckedPageable(offset, limit);
        Page<Post> postsPage;
        if (!stringDate.matches("[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])")) {
            LocalDate localDate = LocalDate.now();
            postsPage = postRepository.findByDate(localDate.toString(), pageable);
        } else {
            postsPage = postRepository.findByDate(stringDate, pageable);
        }
        postResponse.setCount(postsPage.getTotalElements());
        List<Post> postsList = postsPage.getContent();
        postResponse.setPosts(submethodsForService.fillAndGetArrayWithPosts(postsList));

        return postResponse;
    }

    public PostResponse getPostsListByQuery(Integer offset,
                                            Integer limit, String query) {
        PostResponse postResponse = new PostResponse();
        Pageable pageable = submethodsForService.getCheckedPageable(offset, limit);
        Page<Post> postsPage = postRepository.findByTextContaining(query, pageable);
        postResponse.setCount(postsPage.getTotalElements());
        List<Post> postsList = postsPage.getContent();
        postResponse.setPosts(submethodsForService.fillAndGetArrayWithPosts(postsList));

        return postResponse;
    }

    public PostResponse getPostsList(Integer offset,
                                     Integer limit, String mode) {
        PostResponse postResponse = new PostResponse();
        Pageable pageable = submethodsForService.getCheckedPageable(offset, limit);
        Page<Post> page;
        if (mode == null) {
            mode = "RECENT";
        }
        switch (mode) {
            case "POPULAR":
                page = postRepository.findAllAndOrderByCommentariesSize(pageable);
                break;
            case "EARLY":
                page = postRepository.findAllAndOrderByTimeAsc(pageable);
                break;
            case "BEST":
                page = postRepository.findAllAndOrderByVotesCount(pageable);
                break;
            default:
                page = postRepository.findAllAndOrderByTimeDesc(pageable);
        }
        postResponse.setCount(page.getTotalElements());
        List<Post> postsList = page.getContent();
        postResponse.setPosts(submethodsForService.fillAndGetArrayWithPosts(postsList));

        return postResponse;
    }
}
