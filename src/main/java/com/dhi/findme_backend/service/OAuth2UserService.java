package com.dhi.findme_backend.service;

import com.dhi.findme_backend.entity.User;
import com.dhi.findme_backend.repository.UserRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class OAuth2UserService {

    private final UserRepository userRepository;

    public OAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User processOAuth2User(OAuth2User oAuth2User, String provider) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String picture = (String) attributes.get("picture");

        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();
            existingUser.setAvatarUrl(picture);
            return userRepository.save(existingUser);
        }

        User newUser = new User();
        newUser.setEmail(email);
        newUser.setFirstName(extractFirstName(name));
        newUser.setLastName(extractLastName(name));
        newUser.setAvatarUrl(picture);
        newUser.setPassword(generateRandomPassword());
        newUser.setVerified(true);
        newUser.setRole("user");
        newUser.setAddressesCreatedCount(0);
        newUser.setMaxAddresses(4);
        newUser.setPlan("free");
        newUser.setRegistrationDate(LocalDate.now());

        return userRepository.save(newUser);
    }

    private String extractFirstName(String fullName) {
        if (fullName == null || fullName.isEmpty()) {
            return "User";
        }
        String[] parts = fullName.split(" ");
        return parts[0];
    }

    private String extractLastName(String fullName) {
        if (fullName == null || fullName.isEmpty()) {
            return "";
        }
        String[] parts = fullName.split(" ");
        return parts.length > 1 ? parts[parts.length - 1] : "";
    }

    private String generateRandomPassword() {
        return java.util.UUID.randomUUID().toString();
    }
}
