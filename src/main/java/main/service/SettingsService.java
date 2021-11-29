package main.service;
import main.api.response.SettingResponse;
import main.model.repositories.GlobalSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {

    @Autowired
    private GlobalSettingsRepository globalSettingsRepository;

    public SettingResponse getGlobalSettings() {
        SettingResponse settingResponse = new SettingResponse();
        settingResponse.setMultiuserMode(globalSettingsRepository
                .findById(1).get().getValue().contentEquals("Yes"));
        settingResponse.setPostPremoderation(globalSettingsRepository
                .findById(2).get().getValue().contentEquals("Yes"));
        settingResponse.setStatisticsIsPublic(globalSettingsRepository
                .findById(3).get().getValue().contentEquals("Yes"));

        return settingResponse;
    }
}
