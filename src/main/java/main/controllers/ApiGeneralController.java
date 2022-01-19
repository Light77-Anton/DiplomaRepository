package main.controllers;
import main.api.response.*;
import main.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class ApiGeneralController {

    private final InitResponse initResponse;
    private final SettingsService settingsService;
    private final LoginResponse loginResponse;
    private final PostService postService;
    private final CalendarService calendarService;
    private final TagService tagService;
    private final CaptchaService captchaService;

    public ApiGeneralController(InitResponse initResponse,
                                SettingsService settingsService,
                                LoginResponse loginResponse,
                                PostService postService,
                                CalendarService calendarService,
                                TagService tagService,
                                CaptchaService captchaService) {
        this.initResponse = initResponse;
        this.settingsService = settingsService;
        this.loginResponse = loginResponse;
        this.postService = postService;
        this.calendarService = calendarService;
        this.tagService = tagService;
        this.captchaService = captchaService;
    }

    @GetMapping("/api/init")
    public ResponseEntity<InitResponse> init() {
        return new ResponseEntity<>(initResponse, HttpStatus.OK);
    }

    //@GetMapping("/api/settings")
    @RequestMapping(value = "/api/settings", method = { RequestMethod.GET, RequestMethod.POST })
    public ResponseEntity<SettingResponse> settings() {
        return new ResponseEntity<>(settingsService.getGlobalSettings(),
                HttpStatus.OK);
    }

    @GetMapping("/api/tag")
    public ResponseEntity<TagResponse> tag(
            @RequestParam(value = "query", required = false) String query) {

        return new ResponseEntity<>(tagService.getTagList(query),
                HttpStatus.OK);
    }

    @GetMapping("/api/calendar")
    public ResponseEntity calendar(@RequestParam(value = "year",
            required = false) String year) {

        return new ResponseEntity(calendarService.
                getPostsPerYear(year), HttpStatus.OK);
    }

}
