package main.controllers;
import main.api.request.CommentRequest;
import main.api.request.ProfileRequest;
import main.api.request.SettingsRequest;
import main.api.response.*;
import main.dto.PostDTO;
import main.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

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
    public ResponseEntity<Response> comment(@RequestBody CommentRequest commentRequest,
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
    public ResponseEntity<Response> allStatistics(Principal principal) {
        if (settingsService.isAccessToAllStatistics(principal)) {
            return ResponseEntity.ok(settingsService.getAllStatistics());
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PreAuthorize("hasAuthority('user:moderate')")
    @PutMapping("/api/settings")
    public ResponseEntity<Response> changeSettings(@RequestBody SettingsRequest settingsRequest) {
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
    @PostMapping(value = "/api/profile/my", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response> profile(@RequestParam(value = "photo", required = false) MultipartFile photo,
                                     @RequestParam(value = "name", required = false) String name,
                                     @RequestParam(value = "email", required = false) String email,
                                     @RequestParam(value = "password", required = false) String password,
                                     @RequestParam(value = "removePhoto", required = false) boolean removePhoto,
                                     Principal principal) {
        if (profileService.checkProfileChanges(photo, name, email, password, removePhoto, principal).isEmpty()) {
            ResultResponse resultResponse = new ResultResponse();
            resultResponse.setResult(true);
            return ResponseEntity.ok().build();
        }
        FalseResultErrorsResponse profileChangeResponse = new FalseResultErrorsResponse();
        profileChangeResponse.setErrors(profileService.checkProfileChanges(photo, name, email, password, removePhoto, principal));

        return ResponseEntity.ok(profileChangeResponse);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping(value = "/api/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response> uploadImage(@RequestParam(value = "image", required = true) MultipartFile image) {
        List<String> response = postService.uploadImageAndGetLink(image);
        for (String string : response) {
            if (!string.endsWith("jpg") || !string.endsWith("png")) {
                FalseResultErrorsResponse falseResultErrorsResponse = new FalseResultErrorsResponse();
                falseResultErrorsResponse.setErrors(response);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(falseResultErrorsResponse);
            } else {
                ImageLinkResponse imageLinkResponse = new ImageLinkResponse();
                imageLinkResponse.setLink(string);
                return ResponseEntity.ok(imageLinkResponse);
            }
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
