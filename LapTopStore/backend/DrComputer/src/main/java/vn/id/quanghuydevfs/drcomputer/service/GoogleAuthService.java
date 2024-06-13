package vn.id.quanghuydevfs.drcomputer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import vn.id.quanghuydevfs.drcomputer.model.user.Roles;
import vn.id.quanghuydevfs.drcomputer.model.user.User;
import vn.id.quanghuydevfs.drcomputer.repository.UserRepository;

import java.time.LocalDate;

@Service
public class GoogleAuthService {
    @Autowired
    private UserRepository userRepository;

    public User processGoogleUser(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        User user = userRepository.findByEmail(email).get();
        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setFullname(oAuth2User.getAttribute("name"));
            user.setRoles(Roles.USER);
            user.setEnabled(true);
            user.setCreatedAt(LocalDate.now());
        }
        return userRepository.save(user);
    }
}
