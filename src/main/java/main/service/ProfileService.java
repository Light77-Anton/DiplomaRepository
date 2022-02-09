package main.service;
import main.api.request.ProfileRequest;
import main.api.response.StatisticsResponse;
import main.model.User;
import main.model.repositories.PostRepository;
import main.model.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
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

    public StatisticsResponse getMyStatistics(Principal principal) {
        StatisticsResponse myStatisticsResponse = new StatisticsResponse();
        main.model.User currentUser = userRepository.findByEmail
                        (principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        myStatisticsResponse.setPostsCount(currentUser.getPosts().size());
        myStatisticsResponse.setLikesCount(postRepository.findLikesCountByUserId(currentUser.getId()));
        myStatisticsResponse.setDislikesCount(postRepository.findDislikesCountByUserId(currentUser.getId()));
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
        Optional<User> userName = userRepository.findByName(name);
        Optional<User> userEmail = userRepository.findByEmail(email);
        if (name != null && email != null && password != null && photo != null && !profileRequest.isRemovePhoto()) {
            checkName(name, errors);
            checkEmail(email, errors);
            checkPhoto(photo, errors);
            checkPassword(password, errors);
            if (errors.isEmpty()) {
                int updatedRow = userRepository.fullUpdateMyProfile(currentUser.getId(), name, email, getScaledPhoto(photo), password);
            }
            return errors;
        }
        else if (password == null && photo == null && name != null && email != null) {
            checkName(name, errors);
            checkEmail(email, errors);
            if (errors.isEmpty()) {
                int updatedRow = userRepository.nameEmailUpdateMyProfile(currentUser.getId(), name, email);
            }
            return errors;
        }
        else if (photo == null && name != null && email != null && password != null) {
            checkName(name, errors);
            checkEmail(email, errors);
            checkPassword(password, errors);
            if (errors.isEmpty()) {
                int updatedRow = userRepository.passwordUpdateMyProfile(currentUser.getId(), name, email, password);
            }
            return errors;
        }
        else if (password == null && photo != null && name != null && email != null) {
            checkName(name, errors);
            checkEmail(email, errors);
            checkPhoto(photo, errors);
            if (errors.isEmpty()) {
                int updatedRow = userRepository.photoUpdateMyProfile(currentUser.getId(), name, email, getScaledPhoto(photo));
            }
            return errors;
        }
        else if (password == null && removePhoto && name != null && email != null) {
            checkName(name, errors);
            checkEmail(email, errors);
            if (errors.isEmpty()) {
                int updatedRow = userRepository.removePhotoUpdateMyProfile(currentUser.getId(), name, email);
            }
            return errors;
        } else {
            errors.add("Не хватает данных для изменения профиля");
            return errors;
        }
    }

    private void checkName(String name, List<String> errors) {
        Optional<User> userName = userRepository.findByName(name);
        if (name.equals("") || userName.isPresent()) {
            errors.add("Имя не введено или уже существует");
        }
    }

    private void checkEmail(String email, List<String> errors) {
        Optional<User> userEmail = userRepository.findByEmail(email);
        if (!email.contains("@") || userEmail.isPresent()) {
            errors.add("Такой эмэйл уже существует");
        }
    }

    private void checkPassword(String password, List<String> errors) {
        if (password.length() < 6) {
            errors.add("Пароль короче 6-ти символов");
        }
    }

    private void checkPhoto(String photo, List<String> errors) {
        if (!photo.endsWith("jpg") && !photo.endsWith("png")) {
            errors.add("Неподходящий формат фото");
        }
    }

    private String getScaledPhoto(String photo) {
        String scaledPhoto = "";
        try {
            BufferedImage bufferedImage = ImageIO.read(new File(photo));
            Image image = bufferedImage.getScaledInstance(36, 36, Image.SCALE_DEFAULT);
            scaledPhoto = image.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return scaledPhoto;
    }
}
