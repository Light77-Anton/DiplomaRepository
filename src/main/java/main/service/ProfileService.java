package main.service;
import main.api.response.StatisticsResponse;
import main.config.SecurityConfig;
import main.model.Post;
import main.model.User;
import main.model.Vote;
import main.model.repositories.PostRepository;
import main.model.repositories.UserRepository;
import main.model.repositories.VoteRepository;
import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private static final long UPLOAD_LIMIT = 5242880;

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

    public List<String> checkProfileChanges(String name, String email, String password, Byte removePhoto,
                                            MultipartFile photo, Principal principal) {
        main.model.User currentUser = userRepository.findByEmail
                        (principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        List<String> emptyList = new ArrayList<>();
        int userId = currentUser.getId();
        if (photo != null && removePhoto == 0 && name != null && email != null && password != null) { //Запрос c изменением пароля и фотографии
            List<String> listAfterFirstCheck = checkName(name, userId, emptyList);
            List<String> listAfterSecondCheck = checkEmail(email, userId, listAfterFirstCheck);
            List<String> listAfterThirdCheck = checkPassword(password, userId, listAfterSecondCheck);
            List<String> listAfterFoughtCheck = checkPhoto(photo, userId, listAfterThirdCheck);
            return listAfterFoughtCheck;
        }
        if (photo != null && removePhoto == 0 && name != null && email != null) { //Запрос изменение фотографии и изменение данных, без смены пароля
            List<String> listAfterFirstCheck = checkName(name, userId, emptyList);
            List<String> listAfterSecondCheck = checkEmail(email, userId, listAfterFirstCheck);
            List<String> listAfterThirdCheck = checkPhoto(photo, userId, listAfterSecondCheck);
            return listAfterThirdCheck;
        }
        if (photo != null) {
            if (photo.getOriginalFilename().equals("") && removePhoto == 1 && name != null && email != null) { // Запрос на удаление фотографии без изменения пароля
                List<String> listAfterFirstCheck = checkName(name, userId, emptyList);
                List<String> listAfterSecondCheck = checkEmail(email, userId, listAfterFirstCheck);
                if (listAfterSecondCheck.isEmpty()) {
                    removePhoto(userId);
                }
                return listAfterSecondCheck;
            }
        }
        if (name != null && email != null && password != null) { // Запрос c изменением пароля и без изменения фотографии
            List<String> listAfterFirstCheck = checkName(name, userId, emptyList);
            List<String> listAfterSecondCheck = checkEmail(email, userId, listAfterFirstCheck);
            List<String> listAfterThirdCheck = checkPassword(password, userId, listAfterSecondCheck);
            return listAfterThirdCheck;
        }
        if (name != null && email != null) { // Запрос без изменения пароля и фотографии
            List<String> listAfterFirstCheck = checkName(name, userId, emptyList);
            List<String> listAfterSecondCheck = checkEmail(email, userId, listAfterFirstCheck);
            return listAfterSecondCheck;
        }

        return emptyList;
    }

    private List<String> checkName(String name, int userId, List<String> errors) {
        if (name.equals("")) {
            errors.add("Имя введено неверно");
            return errors;
        }
        Optional<User> userWithSpecifiedName = userRepository.findByName(name);
        if (userWithSpecifiedName.isPresent() && userWithSpecifiedName.get().getId() != userId) {
            errors.add("Такое имя уже существует");
            return errors;
        }
        userRepository.updateNameProfile(userId, name);

        return errors;
    }

    private List<String> checkEmail(String email, int userId, List<String> errors) {
        if (!email.contains("@")) {
            errors.add("Это явно не эмэйл");
            return errors;
        }
        Optional<User> userWithSpecifiedEmail = userRepository.findByEmail(email);
        if (userWithSpecifiedEmail.isPresent() && userWithSpecifiedEmail.get().getId() != userId) {
            errors.add("Такой эмэйл уже существует");
            return errors;
        }
        userRepository.updateEmailProfile(userId, email);

        return errors;
    }

    private List<String> checkPassword(String password, int userId, List<String> errors) {
        if (password.length() < 6) {
            errors.add("Пароль короче 6-ти символов");
            return errors;
        }
        userRepository.updatePasswordProfile(userId, securityConfig.passwordEncoder().encode(password));

        return errors;
    }

    private List<String> checkPhoto(MultipartFile photo, int userId, List<String> errors) {
        if (photo.getSize() > UPLOAD_LIMIT) {
            errors.add("Размер изображения должен быть не более 5 МБ");
            return errors;
        }
        if (!photo.getOriginalFilename().endsWith("jpg") && !photo.getOriginalFilename().endsWith("png")) {
            errors.add("Неподходящий формат фото");
            return errors;
        }
        String extension = FilenameUtils.getExtension(photo.getOriginalFilename());
        try {
            BufferedImage bufferedImage = ImageIO.read(photo.getInputStream());
            BufferedImage editedImage = Scalr.resize(bufferedImage, Scalr.Mode.FIT_EXACT,36,36);
            String pathToImage = "avatars/id" + userId + "avatar." + extension;
            Path path = Paths.get(pathToImage);
            ImageIO.write(editedImage, extension, path.toFile());
            userRepository.updatePhotoProfile(userId, path.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return errors;
    }

    private void removePhoto(int userId) {
       userRepository.removePhotoProfile(userId);
    }
}
