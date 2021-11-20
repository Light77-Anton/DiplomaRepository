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

    @GetMapping("/api/auth/check")
    private ResponseEntity<AuthCheckResponse> authCheck() {
        return new ResponseEntity<>(authCheckResponse, HttpStatus.OK);
    }

    @GetMapping("/api/post")
    private ResponseEntity post(
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "mode", required = false) String mode) {

        return new ResponseEntity(postService.getPostsList(offset, limit, mode), HttpStatus.OK);
    }

    @GetMapping("/api/tag")
    private ResponseEntity<TagResponse> tag(
            @RequestParam(value = "query", required = false) String query) {

        return new ResponseEntity<>(tagService.getTagList(query), HttpStatus.OK);
    }

    @GetMapping("/api/post/search")
    private ResponseEntity<PostResponse> postSearch(
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "query", required = false) String query
            ) {

        if (query == null) {
            return new ResponseEntity(post(offset, limit, query), HttpStatus.OK);
        }
        if (query.equals("") || query.matches("\\s+")) {
            return new ResponseEntity(post(offset, limit, query), HttpStatus.OK);
        }

        return new ResponseEntity(postService.getPostsListByQuery(offset, limit, query), HttpStatus.OK);
    }

    @GetMapping("/api/calendar")
    private ResponseEntity calendar(@RequestParam(value = "year", required = false) String year) {

        return new ResponseEntity(calendarService.getPostsPerYear(year),HttpStatus.OK);
    }

    @GetMapping("/api/post/byDate")
    private ResponseEntity postByDate(
            @RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "limit", required = false) Integer limit) {

        return new ResponseEntity(postService.getPostsByDate(offset, limit, date), HttpStatus.OK);
    }

    @GetMapping("/api/post/byTag")
    private ResponseEntity<PostResponse> postByTag(
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "tag", required = false) String tagName,
            @RequestParam(value = "limit", required = false) Integer limit) {

        return new ResponseEntity<>(postService.getPostsByTag(offset, limit, tagName), HttpStatus.OK);
    }

    @GetMapping("/api/post/{ID}")
    private ResponseEntity postById(@PathVariable Integer id) { // пока без авторизации,логика полностью не реализована

        if (postService.getPostById(id) == null) {
            return new ResponseEntity("документ не найден", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(postService.getPostById(id), HttpStatus.OK);
    }

    /*
    @GetMapping("/api/post/moderation")
    private ResponseEntity<PostResponse> postModeration(
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "status") String status) {

        //return new ResponseEntity(tagService.getTagList(), HttpStatus.OK);
    }

     */
}
