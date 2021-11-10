package main.controllers;
import main.api.response.*;
import main.service.PostService;
import main.service.SettingsService;
import main.service.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiGeneralController {

    private final InitResponse initResponse;
    private final SettingsService settingsService;
    private final AuthCheckResponse authCheckResponse;
    private final PostService postService;
    private final TagService tagService;

    public ApiGeneralController(InitResponse initResponse,
                                SettingsService settingsService,
                                AuthCheckResponse authCheckResponse,
                                PostService postService,
                                TagService tagService) {
        this.initResponse = initResponse;
        this.settingsService = settingsService;
        this.authCheckResponse = authCheckResponse;
        this.postService = postService;
        this.tagService = tagService;
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

    @GetMapping("/api/auth/check")
    private ResponseEntity<AuthCheckResponse> authCheck() {
        return new ResponseEntity<>(authCheckResponse, HttpStatus.OK);
    }

    @GetMapping("/api/post")
    private ResponseEntity<PostResponse> post(
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "mode", required = false) String mode) {

        return new ResponseEntity<>(postService.getPostsList(), HttpStatus.OK);
    }

    @GetMapping("/api/tag")
    private ResponseEntity<TagResponse> tag(
            @RequestParam(value = "query", required = false) String query) {

        return new ResponseEntity<>(tagService.getTagList(), HttpStatus.OK);
    }

}
