package main.service;
import main.api.request.SettingsRequest;
import main.api.response.SettingResponse;
import main.api.response.StatisticsResponse;
import main.model.repositories.GlobalSettingsRepository;
import main.model.repositories.PostRepository;
import main.model.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.ZoneOffset;

@Service
public class SettingsService {

    @Autowired
    private GlobalSettingsRepository globalSettingsRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;

    public boolean isMultiuserMode() {
        if (globalSettingsRepository.findMultiuserModeValue().equals("YES")) {
            return true;
        }

        return false;
    }

    public boolean isPremoderation() {
        if (globalSettingsRepository.findPremoderationValue().equals("YES")) {
            return true;
        }

        return false;
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
        main.model.User currentUser = userRepository.findByEmail
                        (principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        if (!isPublic && !currentUser.isModerator()) {
            return false;
        }

        return true;
    }

    public StatisticsResponse getAllStatistics() {
        StatisticsResponse statisticsResponse = new StatisticsResponse();
        statisticsResponse.setPostsCount(postRepository.findAll().size());
        statisticsResponse.setLikesCount(postRepository.findLikesCount());
        statisticsResponse.setDislikesCount(postRepository.findDislikeCount());
        statisticsResponse.setViewsCount(postRepository.findViewCount());
        statisticsResponse.setFirstPublication(postRepository.findTheOldestPublicationTime()
                .toEpochSecond(ZoneOffset.UTC));

        return statisticsResponse;
    }

    public void changeGlobalSettings(SettingsRequest settingsRequest) {
        boolean isMultiuserMode = settingsRequest.isMultiuserMode();
        boolean isPostPremoderation = settingsRequest.isPostPremoderation();
        boolean isStatisticsPublic = settingsRequest.isStatisticsIsPublic();
        if (isMultiuserMode) {
            globalSettingsRepository.setMultiuserMode("YES");
        } else {
            globalSettingsRepository.setMultiuserMode("NO");
        }
        if (isPostPremoderation) {
            globalSettingsRepository.setPostPremoderation("YES");
        } else {
            globalSettingsRepository.setPostPremoderation("NO");
        }
        if (isStatisticsPublic) {
            globalSettingsRepository.setStatistics("YES");
        } else {
            globalSettingsRepository.setStatistics("NO");
        }
    }
}
