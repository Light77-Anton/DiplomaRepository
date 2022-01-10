package main.support;
import main.model.Post;
import main.model.Vote;
import main.model.repositories.PostRepository;
import main.model.repositories.TagRepository;
import main.model.repositories.UserRepository;
import main.support.dto.CountForPostId;
import main.support.dto.PostDTO;
import main.support.dto.UserDataDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SubmethodsForService {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TagRepository tagRepository;

    public SubmethodsForService() {

    }

    /**
     * Добавил дополнительный класс для PostService т.к. сам класс сервис стал неимоверно
     * большим.Здесь будут все вспомогательные методы.
     */

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
            postDTO.setAnnounce(getAnnounce(post.getText()));
            postDTO.setLikesCount(getLikesCount(post));
            postDTO.setDislikeCount(getDislikesCount(post));
            postDTO.setCommentCount(post.getCommentaries().size());
            postDTO.setViewCount(post.getViewCount());
            list.add(postDTO);
        }
        return list;
    }

    public Page<Post> getPostsPageWithRequiredMode(Integer offset,
                                                    Integer limit, Mode mode) {
        List<CountForPostId> bufferArrayList;
        List<Post> postsList;
        Page<Post> postsPage;
        Pageable pageable = PageRequest.of(offset / limit, limit);
        if (mode == Mode.RECENT) {
            postsList = postRepository.findAllAndOrderByTimeDesc(pageable);
            postsPage = checkAndGetPostsPage(postsList);
        } else if (mode == Mode.POPULAR) {
            bufferArrayList = postRepository.findAllAndOrderByCommentariesSize(pageable);
            postsList = new ArrayList<>();
            for (CountForPostId array : bufferArrayList) {
                Integer postId = Integer.valueOf(String.valueOf(array.getId()));
                if (postId == null) {
                    break;
                }
                Optional<Post> post = postRepository.findById(postId);
                postsList.add(post.get());
            }
            postsPage = checkAndGetPostsPage(postsList);
        } else if (mode == Mode.BEST) {
            bufferArrayList = postRepository.findAllAndOrderByVotesCount(pageable);
            postsList = new ArrayList<>();
            for (CountForPostId array : bufferArrayList) {
                Integer postId = Integer.valueOf(String.valueOf(array.getId()));
                if (postId == null) {
                    break;
                }
                Optional<Post> post = postRepository.findById(postId);
                postsList.add(post.get());
            }
            postsPage = checkAndGetPostsPage(postsList);
        } else { // EARLY
            postsList = postRepository.findAllAndOrderByTimeAsc(pageable);
            postsPage = checkAndGetPostsPage(postsList);
        }

        return postsPage;
    }

    public Page<Post> getPostsPageWithRequiredStatus(Integer offset,
                                                     Integer limit, PostStatus postStatus) {

        Page<Post> postsPage = null;
        Pageable pageable = PageRequest.of(offset / limit, limit);
        if (postStatus == PostStatus.INACTIVE) {
            postsPage = postRepository.findAllInactivePosts(pageable);
        }
        else if (postStatus == PostStatus.PENDING) {
            postsPage = postRepository.findAllPendingPosts(pageable);
        }
        else if (postStatus == PostStatus.DECLINED) {
            postsPage = postRepository.findAllDeclinedPosts(pageable);
        }
        else if (postStatus == PostStatus.PUBLISHED) {
            postsPage = postRepository.findAllAcceptedPosts(pageable);
        }

        return postsPage;
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

    public Page<Post> checkAndGetPostsPage(List<Post> bufferPostsList) {
        List<Post> list = new ArrayList<>();
        for (Post post : bufferPostsList) {
            if (post.isActive()
                    && post.getModerationStatus() == ModerationStatus.ACCEPTED
                    && LocalDateTime.now().isAfter(post.getTime())) {
                list.add(post);
            }
        }
        Page<Post> page = new PageImpl<>(list);
        return page;
    }

    public Page<Post> getPostsListWithRequiredDate(Integer offset,
                                                    Integer limit,
                                                    String stringDate) {
        List<Post> bufferPostsList;
        Pageable page = PageRequest.of(offset, limit);
        bufferPostsList = postRepository.findByDate(stringDate, page);
        return checkAndGetPostsPage(bufferPostsList);
    }

    public Page<Post> getPostsListWithRequiredQuery(Integer offset,
                                                     Integer limit,
                                                     String query) {
        List<Post> bufferPostList;
        Pageable page = PageRequest.of(offset, limit, Sort.by("time").descending());
        bufferPostList = postRepository.findByTextContaining(query, page);
        return checkAndGetPostsPage(bufferPostList);
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

    public String getAnnounce(String text) {
        String announce;
        if (text.length() < 150) {
            announce = text + "...";
        } else {
            announce = text.substring(0, 150) + "...";
        }
        return announce;
    }

    public int getLikesCount(Post post) {
        int likesCount = 0;
        for (Vote vote : post.getVotes()) {
            if (vote.getValue() == 1) {
                likesCount++;
            }
        }

        return likesCount;
    }

    public int getDislikesCount(Post post) {
        int dislikesCount = 0;
        for (Vote vote : post.getVotes()) {
            if (vote.getValue() == -1) {
                dislikesCount++;
            }
        }

        return dislikesCount;
    }
}
