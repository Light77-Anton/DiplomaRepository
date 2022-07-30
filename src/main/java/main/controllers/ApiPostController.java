package main.controllers;
import main.api.request.PostRequest;
import main.api.request.VoteRequest;
import main.api.response.*;
import main.model.Post;
import main.model.repositories.UserRepository;
import main.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequestMapping("/api/post")
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

    @GetMapping("")
    public ResponseEntity<PostResponse> post(
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "mode", required = false) String mode) {

        return ResponseEntity.ok(postService.getPostsList(offset,limit,mode));
    }

    @GetMapping("/search")
    public ResponseEntity<PostResponse> postSearch(
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "query", required = false) String query) {
        if (query == null) {
            return ResponseEntity.ok(postService.getPostsList(offset, limit, "recent"));
        }
        if (query.equals("") || query.matches("\\s+")) {
            return ResponseEntity.ok(postService.getPostsList(offset, limit, "recent"));
        }

        return ResponseEntity.ok(postService.getPostsListByQuery(offset, limit, query));
    }

    @GetMapping("/byDate")
    public ResponseEntity<PostResponse> postByDate(
            @RequestParam(value = "date", required = true) String date,
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "limit", required = false) Integer limit) {

        return ResponseEntity.ok(postService.getPostsByDate(offset, limit, date));
    }

    @GetMapping("/byTag")
    public ResponseEntity<PostResponse> postByTag(
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "tag", required = false) String tagName) {
        if (tagName == null) {
            return ResponseEntity.ok(postService.getPostsList(offset, limit, "recent"));
        }

        return ResponseEntity.ok(postService.getPostsByTag(offset, limit, tagName));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostByIdResponse> postById(@PathVariable Integer id, Principal principal) {
        if (principal == null) {
            if (postService.getPostById(id,null) == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(postService.getPostById(id,null));
        }
        main.model.User currentUser = userRepository.findByEmail
                (principal.getName()).orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        if (postService.getPostById(id, currentUser) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(postService.getPostById(id, currentUser));
    }

    @PreAuthorize("hasAuthority('user:write')")
    @GetMapping("/my")
    public ResponseEntity<MyPostResponse> myPost(@RequestParam(value = "offset", required = false) Integer offset,
                                 @RequestParam(value = "limit", required = false) Integer limit,
                                 @RequestParam(value = "status", required = true) String status,
                                 Principal principal) {

        return ResponseEntity.ok(postService.getMyPost(offset, limit, status, principal));
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping("")
    public ResponseEntity<ResultErrorsResponse> post(@RequestBody PostRequest postRequest, Principal principal) {
        if (!postService.post(postRequest, principal).isResult()) {
            return ResponseEntity.ok(postService.post(postRequest, principal));
        }
        ResultErrorsResponse resultErrorsResponse = new ResultErrorsResponse();
        resultErrorsResponse.setResult(true);

        return ResponseEntity.ok(resultErrorsResponse);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PutMapping("/{id}")
    public ResponseEntity<ResultErrorsResponse> updatePost(@PathVariable Integer id,
                                     @RequestBody PostRequest postRequest,
                                     Principal principal) {
        Optional<Post> post = submethodsForService.getOptionalPostByIdAndUserId(id, principal);
        if (post.isEmpty()) {
            ResultErrorsResponse resultErrorsResponse = new ResultErrorsResponse();
            List<String> error = new ArrayList<>();
            error.add("Такого поста не найдено");
            resultErrorsResponse.setErrors(error);
            return ResponseEntity.ok(resultErrorsResponse);
        }

        return ResponseEntity.ok(postService.updatePost(id, postRequest, principal));
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping("/like")
    public ResponseEntity<ResultErrorsResponse> likePost(Principal principal, @RequestBody() VoteRequest voteRequest) {

        return ResponseEntity.ok(postService.setVoteForPost(principal, voteRequest, 1));
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping("/dislike")
    public ResponseEntity<ResultErrorsResponse> dislikePost(Principal principal, @RequestBody VoteRequest voteRequest) {

        return ResponseEntity.ok(postService.setVoteForPost(principal, voteRequest, 0));
    }

    @PreAuthorize("hasAuthority('user:moderate')")
    @GetMapping("/moderation")
    public ResponseEntity<MyPostResponse> findPostsForModeration
            (@RequestParam(value = "offset", required = false) Integer offset,
             @RequestParam(value = "limit", required = false) Integer limit,
             @RequestParam(value = "status", required = true) String status,
             Principal principal) {

        return ResponseEntity.ok(postService.getPostsForModeration(offset, limit, status, principal));
    }
}
