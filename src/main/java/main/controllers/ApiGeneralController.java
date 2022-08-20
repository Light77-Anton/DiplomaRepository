package main.controllers;
import main.api.request.CommentRequest;
import main.api.request.PostModerateRequest;
import main.api.request.SettingsRequest;
import main.api.response.*;
import main.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/api/")
@Controller
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

    @GetMapping("init")
    public ResponseEntity<InitResponse> init() {

        return ResponseEntity.ok(initResponse);
    }

    @GetMapping("settings")
    public ResponseEntity<SettingResponse> settings() {

        return ResponseEntity.ok(settingsService.getGlobalSettings());
    }

    @GetMapping("tag")
    public ResponseEntity<TagResponse> tag(@RequestParam(value = "query", required = false) String query) {
        tagService.checkAndDeleteUnusedTags();

        return ResponseEntity.ok(tagService.getTagList(query));
    }

    @GetMapping("calendar")
    public ResponseEntity<CalendarResponse> calendar(@RequestParam(value = "year",
            required = false) String year) {

        return ResponseEntity.ok(calendarService.getPostsPerYear(year));
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping("comment")
    public ResponseEntity<CommentResponse> comment(@RequestBody CommentRequest commentRequest,
                                  Principal principal) {
        List<String> errors = submethodsForService.checkAndAddComment(commentRequest, principal);
        CommentResponse commentResponse = new CommentResponse();
        String possibleId = errors.get(0);
        if (possibleId.substring(0, 2).equals("id")) {
            commentResponse.setId(Integer.parseInt(possibleId.substring(5)));
            commentResponse.setResult(null);
            commentResponse.setErrors(null);
            return ResponseEntity.ok(commentResponse);
        }
        commentResponse.setId(null);
        commentResponse.setResult(false);
        commentResponse.setErrors(errors);

        return ResponseEntity.ok(commentResponse);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @GetMapping("statistics/my")
    public ResponseEntity<StatisticsResponse> myStatistics(Principal principal) {

        return ResponseEntity.ok(profileService.getMyStatistics(principal));
    }

    @GetMapping("statistics/all")
    public ResponseEntity<StatisticsResponse> allStatistics(Principal principal) {
        if (settingsService.isAccessToAllStatistics(principal)) {
            return ResponseEntity.ok(settingsService.getAllStatistics());
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PreAuthorize("hasAuthority('user:moderate')")
    @PutMapping("settings")
    public ResponseEntity<HttpStatus> changeSettings(@RequestBody SettingsRequest settingsRequest) {
        settingsService.changeGlobalSettings(settingsRequest);

        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping(value = "profile/my", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE,MediaType.APPLICATION_JSON_VALUE }
            , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResultErrorsResponse> profile(@RequestPart(value = "name") String name,
                                     @RequestPart(value = "email") String email,
                                     @RequestPart(value = "photo", required = false) MultipartFile photo,
                                     @RequestPart(value = "removePhoto", required = false) String removePhoto,
                                     @RequestPart(value = "password", required = false) String password,
                                     Principal principal) {
        if (profileService.checkProfileChanges(name, email, password, removePhoto, photo, principal).isEmpty()) {
            ResultErrorsResponse resultResponse = new ResultErrorsResponse();
            resultResponse.setResult(true);
            return ResponseEntity.ok(resultResponse);
        }
        ResultErrorsResponse response = new ResultErrorsResponse();
        response.setErrors(profileService.checkProfileChanges(name, email, password, removePhoto, photo, principal));

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping(value = "image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageResponse> uploadImage(@RequestPart(value = "image", required = true) MultipartFile image) {
        String result = postService.uploadImageAndGetLink(image);
        List<String> error = new ArrayList<>();
        ImageResponse imageResponse = new ImageResponse();
        if (result.equals("Размер изображения должен быть не более 5 МБ")
                || result.equals("Неподходящий формат изображения")) {
            error.add(result);
            imageResponse.setImage(null);
            imageResponse.setErrors(error);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(imageResponse);
        }
        imageResponse.setImage(result);
        imageResponse.setResult(null);
        imageResponse.setErrors(null);

        return ResponseEntity.ok(imageResponse);
    }

    @PreAuthorize("hasAuthority('user:moderate')")
    @PostMapping("/moderation")
    public ResponseEntity<ResultErrorsResponse> moderatePost(Principal principal,
                                                             @RequestBody PostModerateRequest postModerateRequest) {

        return ResponseEntity.ok(postService.checkModeratorDecision(postModerateRequest, principal));
    }
}
