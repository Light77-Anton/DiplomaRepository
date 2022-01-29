package main.service;
import main.api.response.LoginResponse;
import main.model.repositories.PostRepository;
import main.model.repositories.UserRepository;
import main.support.dto.LoginDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    Logger logger = LogManager.getLogger(AuthService.class);
    Marker marker = MarkerManager.getMarker("CORRECT");
    private final AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;

    public AuthService(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public LoginResponse getLoginResponse(String email) {
        main.model.User currentUser = userRepository.findByEmail
                (email).orElseThrow(() -> new UsernameNotFoundException(email));
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setResult(true);
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setId(currentUser.getId());
        loginDTO.setName(currentUser.getName());
        loginDTO.setEmail(currentUser.getEmail());
        loginDTO.setPhoto(currentUser.getPhoto());
        loginDTO.setModeration(currentUser.isModerator());
        if (currentUser.isModerator()) {
            loginDTO.setModerationCount(postRepository.findAllNewPosts().size());
        }
        else {
            loginDTO.setModerationCount(null);
        }
        loginDTO.setSettings(false);
        loginResponse.setUserData(loginDTO);

        return loginResponse;
    }

    public LoginResponse getLogin(String loginEmail, String loginPassword) {
        logger.info(marker, "Переданные данные : " + loginEmail + " " + loginPassword);
        Authentication auth = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken
                        (loginEmail, loginPassword));
        SecurityContextHolder.getContext().setAuthentication(auth);
        User user = (User) auth.getPrincipal();

        return getLoginResponse(user.getUsername());
    }

    public boolean getLogout() {
        Boolean result = true;
        SecurityContextHolder.clearContext();

        return result;
    }
}
