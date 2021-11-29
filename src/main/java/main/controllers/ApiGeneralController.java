package main.controllers;
import main.api.response.*;
import main.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ApiGeneralController {

    private final InitResponse initResponse;
    private final SettingsService settingsService;
    private final AuthCheckResponse authCheckResponse;
    private final PostService postService;
    private final CalendarService calendarService;
    private final TagService tagService;
    private final CaptchaService captchaService;

    public ApiGeneralController(InitResponse initResponse,
                                SettingsService settingsService,
                                AuthCheckResponse authCheckResponse,
                                PostService postService,
                                CalendarService calendarService,
                                TagService tagService,
                                CaptchaService captchaService) {
        this.initResponse = initResponse;
        this.settingsService = settingsService;
        this.authCheckResponse = authCheckResponse;
        this.postService = postService;
        this.calendarService = calendarService;
        this.tagService = tagService;
        this.captchaService = captchaService;
    }

    @GetMapping("/api/init")
    private ResponseEntity<InitResponse> init() {
        return new ResponseEntity<>(initResponse, HttpStatus.OK);
    }

    @GetMapping("/api/settings")
    private ResponseEntity<SettingResponse> settings() {
        return new ResponseEntity<>(settingsService.getGlobalSettings(),
                HttpStatus.OK);
    }

    @GetMapping("/api/tag")
    private ResponseEntity<TagResponse> tag(
            @RequestParam(value = "query", required = false) String query) {

        return new ResponseEntity<>(tagService.getTagList(query),
                HttpStatus.OK);
    }

    @GetMapping("/api/calendar")
    private ResponseEntity calendar(@RequestParam(value = "year",
            required = false) String year) {

        return new ResponseEntity(calendarService.
                getPostsPerYear(year), HttpStatus.OK);
    }

}
