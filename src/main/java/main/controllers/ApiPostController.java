package main.controllers;
import main.api.response.PostResponse;
import main.service.CaptchaService;
import main.service.PostService;
import main.service.RegisterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
//@RequestMapping(/api/post/)  предположительно, здесь будет все,что связано с post
public class ApiPostController {

    private final RegisterService registerService;
    private final CaptchaService captchaService;
    private final PostService postService;

    public ApiPostController(RegisterService registerService,
                             CaptchaService captchaService, PostService postService) {
        this.registerService = registerService;
        this.captchaService = captchaService;
        this.postService = postService;
    }

    @GetMapping("/api/post")
    @PreAuthorize("permitAll()")
    private ResponseEntity post(
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "mode", required = false) String mode) {

        return new ResponseEntity(postService.getPostsList(offset, limit, mode),
                HttpStatus.OK);
    }

    @GetMapping("/api/post/search")
    @PreAuthorize("permitAll()")
    private ResponseEntity<PostResponse> postSearch(
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "query", required = false) String query
    ) {

        if (query == null) {
            return new ResponseEntity(post(offset, limit, query),
                    HttpStatus.OK);
        }
        if (query.equals("") || query.matches("\\s+")) {
            return new ResponseEntity(post(offset, limit, query),
                    HttpStatus.OK);
        }

        return new ResponseEntity(postService.
                getPostsListByQuery(offset, limit, query), HttpStatus.OK);
    }

    @GetMapping("/api/post/byDate")
    @PreAuthorize("permitAll()")
    private ResponseEntity postByDate(
            @RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "limit", required = false) Integer limit) {

        return new ResponseEntity(postService.
                getPostsByDate(offset, limit, date), HttpStatus.OK);
    }

    @GetMapping("/api/post/byTag")
    @PreAuthorize("permitAll()")
    private ResponseEntity<PostResponse> postByTag(
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "tag", required = false) String tagName) {
        if (tagName == null) {
            return new ResponseEntity(post(offset, limit, tagName),
                    HttpStatus.OK);
        }

        return new ResponseEntity<>(postService.
                getPostsByTag(offset, limit, tagName), HttpStatus.OK);
    }

    @GetMapping("/api/post/{id}")
    @PreAuthorize("permitAll()")
    private ResponseEntity postById(@PathVariable Integer id) { // пока без авторизации,логика полностью не реализована

        if (postService.getPostById(id) == null) {
            return new ResponseEntity("документ не найден",
                    HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(postService.getPostById(id), HttpStatus.OK);
    }

    @GetMapping("/api/post/my")
    @PreAuthorize("user:write")
    private ResponseEntity myPost(@RequestParam(value = "offset", required = false) Integer offset,
                                  @RequestParam(value = "limit", required = false) Integer limit,
                                  @RequestParam(value = "status", required = true) String status) {

        return new ResponseEntity(postService.getMyPost(offset, limit, status), HttpStatus.OK);
    }

}
