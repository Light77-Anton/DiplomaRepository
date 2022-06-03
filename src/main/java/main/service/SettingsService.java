package main.service;
import main.api.request.SettingsRequest;
import main.api.response.SettingResponse;
import main.api.response.StatisticsResponse;
import main.model.Post;
import main.model.Vote;
import main.model.repositories.GlobalSettingsRepository;
import main.model.repositories.PostRepository;
import main.model.repositories.UserRepository;
import main.model.repositories.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class SettingsService {

    @Autowired
    private GlobalSettingsRepository globalSettingsRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private VoteRepository voteRepository;

    public boolean isMultiuserMode() {

        return globalSettingsRepository.findMultiuserModeValue().equals("YES");
    }

    public boolean isPremoderation() {

        return globalSettingsRepository.findPremoderationValue().equals("YES");
    }

    public SettingResponse getGlobalSettings() {
        SettingResponse settingResponse = new SettingResponse();
        settingResponse.setMultiuserMode(globalSettingsRepository
                .findById(1).get().getValue().contentEquals("YES"));
        settingResponse.setPostPremoderation(globalSettingsRepository
                .findById(2).get().getValue().contentEquals("YES"));
        settingResponse.setStatisticsIsPublic(globalSettingsRepository
                .findById(3).get().getValue().contentEquals("YES"));

        return settingResponse;
    }

    public boolean isAccessToAllStatistics(Principal principal) {
        boolean isPublic = globalSettingsRepository
                .findById(3).get().getValue().contentEquals("YES");
        if (!isPublic) {
            if (principal == null) {
                return false;
            } else {
                main.model.User currentUser = userRepository.findByEmail
                                (principal.getName())
                        .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
                if (!currentUser.isModerator()) {
                    return false;
                }
            }
        }

        return true;
    }

    public StatisticsResponse getAllStatistics() {
        StatisticsResponse statisticsResponse = new StatisticsResponse();
        List<Post> postsList = postRepository.findAllActivePosts();
        int likesCount = 0;
        int dislikesCount = 0;
        int viewCount = 0;
        LocalDateTime ldt = null;
        for (Post post : postsList) {
            for (Vote vote : post.getVotes()) {
                if (vote.getValue() == 1) {
                    likesCount++;
                } else {
                    dislikesCount++;
                }
            }
            int currentPostViewCount = post.getViewCount();
            viewCount += currentPostViewCount;
            if (ldt == null || post.getTime().isBefore(ldt)) {
                ldt = post.getTime();
            }
        }
        statisticsResponse.setPostsCount(postsList.size());
        statisticsResponse.setLikesCount(likesCount);
        statisticsResponse.setDislikesCount(dislikesCount);
        statisticsResponse.setViewsCount(viewCount);
        if (ldt == null) {
            statisticsResponse.setFirstPublication(0);
            return statisticsResponse;
        }
        statisticsResponse.setFirstPublication(ldt.toEpochSecond(ZoneOffset.UTC));

        return statisticsResponse;
    }

    public void changeGlobalSettings(SettingsRequest settingsRequest) {
        if (settingsRequest.getMultiuserMode() != null) {
            if (settingsRequest.getMultiuserMode()) {
                globalSettingsRepository.setMultiuserMode("YES");
            } else {
                globalSettingsRepository.setMultiuserMode("NO");
            }
        }
        if (settingsRequest.getPostPremoderation() != null) {
            if (settingsRequest.getPostPremoderation()) {
                globalSettingsRepository.setPostPremoderation("YES");
            } else {
                globalSettingsRepository.setPostPremoderation("NO");
            }
        }
        if (settingsRequest.getStatisticsIsPublic() != null) {
            if (settingsRequest.getStatisticsIsPublic()) {
                globalSettingsRepository.setStatistics("YES");
            } else {
                globalSettingsRepository.setStatistics("NO");
            }
        }
    }
}
