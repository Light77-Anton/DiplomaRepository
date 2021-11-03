package main.service;
import main.api.response.SettingResponse;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {

    public SettingResponse getGlobalSettings(){

        SettingResponse settingResponse = new SettingResponse();
        settingResponse.setMultiuserMode(true);
        settingResponse.setPostPremoderation(true);
        settingResponse.setStatisticsIsPublic(true);
        return settingResponse;
    }
}
