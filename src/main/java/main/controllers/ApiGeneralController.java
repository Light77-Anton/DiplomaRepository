package main.controllers;
import main.api.request.CommentRequest;
import main.api.request.ProfileRequest;
import main.api.request.SettingsRequest;
import main.api.response.*;
import main.dto.PostDTO;
import main.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
public class ApiGeneralController {

    private final InitResponse initResponse;
    private final SettingsService settingsService;
    private final LoginResponse loginResponse;
    private final PostService postService;
    private final CalendarService calendarService;
    private final TagService tagService;
    private final CaptchaService captchaService;
    private final SubmethodsForService submethodsForService;
    private final ProfileService profileService;

    public ApiGeneralController(InitResponse initResponse,
                                SettingsService settingsService,
                                LoginResponse loginResponse,
                                PostService postService,
                                CalendarService calendarService,
                                TagService tagService,
                                CaptchaService captchaService, SubmethodsForService submethodsForService, ProfileService profileService) {
        this.initResponse = initResponse;
        this.settingsService = settingsService;
        this.loginResponse = loginResponse;
        this.postService = postService;
        this.calendarService = calendarService;
        this.tagService = tagService;
        this.captchaService = captchaService;
        this.submethodsForService = submethodsForService;
        this.profileService = profileService;
    }

    @GetMapping("/api/init")
    public ResponseEntity<InitResponse> init() {

        return ResponseEntity.ok(initResponse);
    }

    @GetMapping("/api/settings")
    public ResponseEntity<SettingResponse> settings() {

        return ResponseEntity.ok(settingsService.getGlobalSettings());
    }

    @GetMapping("/api/tag")
    public ResponseEntity<TagResponse> tag(
            @RequestParam(value = "query", required = false) String query) {

        return ResponseEntity.ok(tagService.getTagList(query));
    }

    @GetMapping("/api/calendar")
    public ResponseEntity<CalendarResponse> calendar(@RequestParam(value = "year",
            required = false) String year) {

        return ResponseEntity.ok(calendarService.getPostsPerYear(year));
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping("/api/comment")
    public ResponseEntity<?> comment(@RequestBody CommentRequest commentRequest,
                                  Principal principal) {
        if (submethodsForService.checkAndAddComment(commentRequest, principal).isEmpty()) {
            return ResponseEntity.ok(submethodsForService.getSuccessCommentId(principal));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(submethodsForService.getFailedCommentWithErrors
                        (submethodsForService.checkAndAddComment(commentRequest, principal)));
    }

    @PreAuthorize("hasAuthority('user:write')")
    @GetMapping("/api/statistics/my")
    public ResponseEntity<StatisticsResponse> myStatistics(Principal principal) {

        return ResponseEntity.ok(profileService.getMyStatistics(principal));
    }

    @GetMapping("/api/statistics/all")
    public ResponseEntity<?> allStatistics(Principal principal) {
        if (settingsService.isAccessToAllStatistics(principal)) {
            return ResponseEntity.ok(settingsService.getAllStatistics());
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PreAuthorize("hasAuthority('user:moderate')")
    @PutMapping("/api/settings")
    public ResponseEntity<?> changeSettings(@RequestBody SettingsRequest settingsRequest) {
        settingsService.changeGlobalSettings(settingsRequest);

        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('user:moderate')")
    @PostMapping("/api/tag")
    public ResponseEntity<ResultResponse> addNewTag(@RequestBody String name) {
        ResultResponse resultResponse = new ResultResponse();
        if (!tagService.checkAndAddTag(name)) {
            return ResponseEntity.ok(resultResponse);
        }
        resultResponse.setResult(true);

        return ResponseEntity.ok(resultResponse);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping("/api/profile/my")
    public ResponseEntity<?> profile(Principal principal,
                                     @RequestBody ProfileRequest profileRequest) { // есть проблема с фотографией
        if (profileService.checkProfileChanges(profileRequest, principal).isEmpty()) {
            ResultResponse resultResponse = new ResultResponse();
            resultResponse.setResult(true);
            return ResponseEntity.ok(resultResponse);
        }
        FalseResultErrorsResponse profileChangeResponse = new FalseResultErrorsResponse();
        profileChangeResponse.setErrors(profileService.checkProfileChanges(profileRequest, principal));

        return ResponseEntity.ok(profileChangeResponse);
    }
}
