package main.service;
import main.api.request.ProfileRequest;
import main.api.response.StatisticsResponse;
import main.config.SecurityConfig;
import main.model.User;
import main.model.repositories.PostRepository;
import main.model.repositories.UserRepository;
import main.model.repositories.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.Principal;
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
        myStatisticsResponse.setPostsCount(currentUser.getPosts().size());
        myStatisticsResponse.setLikesCount(voteRepository.findLikesCountByUserId(currentUser.getId()));
        myStatisticsResponse.setDislikesCount(voteRepository.findDislikesCountByUserId(currentUser.getId()));
        myStatisticsResponse.setViewsCount(postRepository.findViewCountByUserId(currentUser.getId()));
        myStatisticsResponse.setFirstPublication(postRepository.findTheOldestPublicationTimeByUserId
                (currentUser.getId()).toEpochSecond(ZoneOffset.UTC));

        return myStatisticsResponse;
    }

    public List<String> checkProfileChanges(ProfileRequest profileRequest, Principal principal) {
        main.model.User currentUser = userRepository.findByEmail
                        (principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        List<String> errors = new ArrayList<>();
        String name = profileRequest.getName();
        String email = profileRequest.getEmail();
        String password = profileRequest.getPassword();
        String photo = profileRequest.getPhoto();
        boolean removePhoto = profileRequest.isRemovePhoto();
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
        int updatedRow = userRepository.updateNameProfile(userId, name);

        return null;
    }

    private String checkEmail(String email, int userId) {
        Optional<User> userEmail = userRepository.findByEmail(email);
        if (!email.contains("@") || userEmail.isPresent()) {
            return "Такой эмэйл уже существует";
        }
        int updatedRow = userRepository.updateEmailProfile(userId, email);

        return null;
    }

    private String checkPassword(String password, int userId) {
        if (password.length() < 6) {
            return ("Пароль короче 6-ти символов");
        }
        int updatedRow = userRepository.updatePasswordProfile(userId, securityConfig.passwordEncoder().encode(password));

        return null;
    }

    private String checkPhoto(String photo, int userId) {
        if (!photo.endsWith("jpg") && !photo.endsWith("png")) {
            return "Неподходящий формат фото";
        }
        String scaledPhoto = "";
        try {
            InputStream isInput = new FileInputStream(photo);
            BufferedImage bufferedImage = ImageIO.read(isInput); // под вопросом,непонятно почему не работает,не может найти передаваемый путь до файла.При этом в браузере все нормально.
            Image image = bufferedImage.getScaledInstance(36, 36, Image.SCALE_DEFAULT);
            scaledPhoto = image.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        int updatedRow = userRepository.updatePhotoProfile(userId, scaledPhoto);

        return null;
    }

    private void removePhoto(int userId) {
       int updatedRow = userRepository.removePhotoProfile(userId);
    }
}
