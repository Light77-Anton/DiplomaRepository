package main.service;
import main.api.response.StatisticsResponse;
import main.config.SecurityConfig;
import main.model.Post;
import main.model.User;
import main.model.Vote;
import main.model.repositories.PostRepository;
import main.model.repositories.UserRepository;
import main.model.repositories.VoteRepository;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Configurable
public class ProfileService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private SecurityConfig securityConfig;
    @Autowired
    private VoteRepository voteRepository;

    public StatisticsResponse getMyStatistics(Principal principal) {
        StatisticsResponse myStatisticsResponse = new StatisticsResponse();
        main.model.User currentUser = userRepository.findByEmail
                        (principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        List<Post> postsList = postRepository.findAllMyPosts(currentUser.getId());
        int likesCount = 0;
        int dislikesCount = 0;
        int viewCount = 0;
        LocalDateTime ldt = null;
        for (Post post : postsList) {
            for (Vote vote : post.getVotes()) {
                if (vote.getValue() == 1) {
                    likesCount++;
                } else {
                    dislikesCount++;
                }
            }
            int currentPostViewCount = post.getViewCount();
            viewCount += currentPostViewCount;
            if (ldt == null || post.getTime().isBefore(ldt)) {
                ldt = post.getTime();
            }
        }
        myStatisticsResponse.setPostsCount(postsList.size());
        myStatisticsResponse.setLikesCount(likesCount);
        myStatisticsResponse.setDislikesCount(dislikesCount);
        myStatisticsResponse.setViewsCount(viewCount);
        if (ldt == null) {
            myStatisticsResponse.setFirstPublication(0);
            return myStatisticsResponse;
        }
        myStatisticsResponse.setFirstPublication(ldt.toEpochSecond(ZoneOffset.UTC));

        return myStatisticsResponse;
    }

    public List<String> checkProfileChanges(MultipartFile photo, String name, String email, String password, boolean removePhoto, Principal principal) {
        main.model.User currentUser = userRepository.findByEmail
                        (principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        List<String> errors = new ArrayList<>();
        int userId = currentUser.getId();
        if (name != null) {
           String quote = checkName(name, userId);
           if (quote != null) {
               errors.add(quote);
           }
        }
        if (email != null) {
            String quote = checkEmail(email, userId);
            if (quote != null) {
                errors.add(quote);
            }
        }
        if (password != null) {
            String quote = checkPassword(password, userId);
            if (quote != null) {
                errors.add(quote);
            }
        }
        if (photo != null && !removePhoto) {
            String quote = checkPhoto(photo, userId);
            if (quote != null) {
                errors.add(quote);
            }
        }
        if (removePhoto) {
            removePhoto(userId);
        }

        return errors;
    }

    private String checkName(String name, int userId) {
        Optional<User> userName = userRepository.findByName(name);
        if (name.equals("") || userName.isPresent()) {
            return "Такое имя уже существует";
        }
        userRepository.updateNameProfile(userId, name);

        return null;
    }

    private String checkEmail(String email, int userId) {
        Optional<User> userEmail = userRepository.findByEmail(email);
        if (!email.contains("@") || userEmail.isPresent()) {
            return "Такой эмэйл уже существует";
        }
        userRepository.updateEmailProfile(userId, email);

        return null;
    }

    private String checkPassword(String password, int userId) {
        if (password.length() < 6) {
            return ("Пароль короче 6-ти символов");
        }
        userRepository.updatePasswordProfile(userId, securityConfig.passwordEncoder().encode(password));

        return null;
    }

    private String checkPhoto(MultipartFile photo, int userId) {
        if (!photo.getOriginalFilename().endsWith("jpg") && !photo.getOriginalFilename().endsWith("png")) {
            return "Неподходящий формат фото";
        }
        String scaledPhoto = "";
        try {
            BufferedImage bufferedImage = ImageIO.read(photo.getInputStream());
            Image img = Scalr.resize(bufferedImage,36,36);
            scaledPhoto = img.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        userRepository.updatePhotoProfile(userId, scaledPhoto);

        return null;
    }

    private void removePhoto(int userId) {
       userRepository.removePhotoProfile(userId);
    }
}
