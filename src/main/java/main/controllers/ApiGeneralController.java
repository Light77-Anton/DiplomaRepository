package main.controllers;
import main.api.request.CommentRequest;
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
import java.util.Properties;

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
    public ResponseEntity<TagResponse> tag(
            @RequestParam(value = "query", required = false) String query) {

        return ResponseEntity.ok(tagService.getTagList(query));
    }

    @GetMapping("calendar")
    public ResponseEntity<CalendarResponse> calendar(@RequestParam(value = "year",
            required = false) String year) {

        return ResponseEntity.ok(calendarService.getPostsPerYear(year));
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping("comment")
    public ResponseEntity<ResultDescriptionResponse> comment(@RequestBody CommentRequest commentRequest,
                                  Principal principal) {
        ResultDescriptionResponse response = submethodsForService.checkAndAddComment(commentRequest, principal);
        for (String string : response.getDescription()) {
            if (!string.equals("Ваш комментарий добавлен")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        }

        return ResponseEntity.ok(response);
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

    @PreAuthorize("hasAuthority('user:moderate')")
    @PostMapping("tag")
    public ResponseEntity<ResultResponse> addNewTag(@RequestBody String name) {
        ResultResponse resultResponse = new ResultResponse();
        if (!tagService.checkAndAddTag(name)) {
            return ResponseEntity.ok(resultResponse);
        }
        resultResponse.setResult(true);

        return ResponseEntity.ok(resultResponse);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping(value = "profile/my", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResultDescriptionResponse> profile
            (@RequestParam(value = "avatar", required = false) MultipartFile avatar,
                                     @RequestParam(value = "name", required = false) String name,
                                     @RequestParam(value = "email", required = false) String email,
                                     @RequestParam(value = "password", required = false) String password,
                                     @RequestParam(value = "remove_photo", required = false) boolean removePhoto,
                                     Principal principal) {
        ResultDescriptionResponse response = new ResultDescriptionResponse();
        if (profileService.checkProfileChanges(avatar, name, email, password, removePhoto, principal).isEmpty()) {
            response.setResult(true);
            return ResponseEntity.ok(response);
        }
        response.setDescription(profileService.checkProfileChanges(avatar, name, email, password, removePhoto, principal));

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping(value = "image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResultDescriptionResponse> uploadImage
            (@RequestParam(value = "image", required = true) MultipartFile image) {
        List<String> result = new ArrayList<>();
        ResultDescriptionResponse response = new ResultDescriptionResponse();
        if (postService.uploadImageAndGetLink(image).equals("Размер изображения должен быть не более 5 МБ")
                || postService.uploadImageAndGetLink(image).equals("Неподходящий формат изображения")) {
            response.setDescription(result);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        response.setResult(true);
        response.setDescription(result);

        return ResponseEntity.ok(response);
    }
}
