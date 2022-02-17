package main.controllers;
import main.api.request.PostModerateRequest;
import main.api.request.PostRequest;
import main.api.request.ProfileRequest;
import main.api.request.VoteForPostRequest;
import main.api.response.*;
import main.model.ModerationStatus;
import main.model.repositories.UserRepository;
import main.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

//@RequestMapping(/api/post/)  предположительно, здесь будет все,что связано с post
@Controller
public class ApiPostController {

    private final RegisterService registerService;
    private final CaptchaService captchaService;
    private final PostService postService;
    private final SubmethodsForService submethodsForService;
    private final UserRepository userRepository;
    private final ProfileService profileService;
    private final SettingsService settingsService;

    public ApiPostController(RegisterService registerService,
                             CaptchaService captchaService,
                             PostService postService,
                             SubmethodsForService submethodsForService,
                             UserRepository userRepository, ProfileService profileService, SettingsService settingsService) {
        this.registerService = registerService;
        this.captchaService = captchaService;
        this.postService = postService;
        this.submethodsForService = submethodsForService;
        this.userRepository = userRepository;
        this.profileService = profileService;
        this.settingsService = settingsService;
    }

    @GetMapping("/api/post")
    public ResponseEntity<PostResponse> post(
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "mode", required = false) String mode) {

        return ResponseEntity.ok(postService.getPostsList(offset,limit,mode));
    }

    @GetMapping("/api/post/search")
    public ResponseEntity<PostResponse> postSearch(
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "query", required = false) String query) {
        if (query == null) {
            return ResponseEntity.ok(postService.getPostsList(offset, limit, "RECENT"));
        }
        if (query.equals("") || query.matches("\\s+")) {
            return ResponseEntity.ok(postService.getPostsList(offset, limit, "RECENT"));
        }

        return ResponseEntity.ok(postService.getPostsListByQuery(offset, limit, query));
    }

    @GetMapping("/api/post/byDate")
    public ResponseEntity<PostResponse> postByDate(
            @RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "limit", required = false) Integer limit) {

        return ResponseEntity.ok(postService.getPostsByDate(offset, limit, date));
    }

    @GetMapping("/api/post/byTag")
    public ResponseEntity<PostResponse> postByTag(
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "tag", required = false) String tagName) {
        if (tagName == null) {
            return ResponseEntity.ok(postService.getPostsList(offset, limit, "RECENT"));
        }

        return ResponseEntity.ok(postService.getPostsByTag(offset, limit, tagName));
    }

    @GetMapping("/api/post/{id}")
    public ResponseEntity<?> postById(@PathVariable Integer id, Principal principal) {
        if (principal == null) {
            return ResponseEntity.ok(postService.getPostById(id,null));
        }
        main.model.User currentUser = userRepository.findByEmail
                (principal.getName()).orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        if (postService.getPostById(id, currentUser) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("документ не найден");
        }

        return ResponseEntity.ok(postService.getPostById(id, currentUser));
    }

    @PreAuthorize("hasAuthority('user:write')")
    @GetMapping("/api/post/my")
    public ResponseEntity<?> myPost(@RequestParam(value = "offset", required = false) Integer offset,
                                 @RequestParam(value = "limit", required = false) Integer limit,
                                 @RequestParam(value = "status", required = true) String status,
                                 Principal principal) {

        if (submethodsForService.checkAndGetPostStatus(status) == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Такого статуса не существует");
        }

        return ResponseEntity.ok(postService.getMyPost(offset, limit, status, principal));
    }

    @PreAuthorize("hasAuthority('user:moderate')")
    @GetMapping("/api/post/moderation")
    public ResponseEntity<MyPostResponse> findPostsForModeration(@RequestParam(value = "offset", required = false) Integer offset,
                                                                 @RequestParam(value = "limit", required = false) Integer limit,
                                                                 @RequestParam(value = "status", required = true) ModerationStatus status,
                                                                 Principal principal) {

        return ResponseEntity.ok(postService.getPostsForModeration(offset, limit, status, principal));
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping("/api/post")
    public ResponseEntity<PostResultResponse> post(@RequestBody PostRequest postRequest, Principal principal) {

        return ResponseEntity.ok(postService.post(postRequest, principal));
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PutMapping("/api/post/{id}")
    public ResponseEntity<?> updatePost(@PathVariable Integer id,
                                     @RequestBody PostRequest postRequest,
                                     Principal principal) {
        if (!postService.updatePost(id, postRequest, principal).isResult()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("документ не найден");
        }

        return ResponseEntity.ok(postService.updatePost(id, postRequest, principal));
    }

    @PreAuthorize("hasAuthority('user:moderate')")
    @PostMapping("/api/moderation")
    public ResponseEntity<?> moderatePost(Principal principal,
                                       @RequestBody PostModerateRequest postModerateRequest) {

        return ResponseEntity.ok(postService.checkModeratorDecision(postModerateRequest, principal));
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping("/api/post/like")
    public ResponseEntity<ResultResponse> likePost(Principal principal,
                               @RequestBody VoteForPostRequest voteForPostRequest) {

        return ResponseEntity.ok(postService.setLikeForPost(principal, voteForPostRequest));
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping("/api/post/dislike")
    public ResponseEntity<ResultResponse> dislikePost(Principal principal,
                                   @RequestBody VoteForPostRequest voteForPostRequest) {

        return ResponseEntity.ok(postService.setDislikeForPost(principal, voteForPostRequest));
    }
}
