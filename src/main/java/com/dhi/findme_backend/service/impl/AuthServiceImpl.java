package com.dhi.findme_backend.service.impl;

import com.dhi.findme_backend.dto.AuthGoogleRequest;
import com.dhi.findme_backend.dto.AuthLoginRequest;
import com.dhi.findme_backend.dto.AuthRegisterRequest;
import com.dhi.findme_backend.dto.AuthResponse;
import com.dhi.findme_backend.dto.ForgotPasswordRequest;
import com.dhi.findme_backend.entity.PasswordResetToken;
import com.dhi.findme_backend.entity.User;
import com.dhi.findme_backend.exception.BusinessException;
import com.dhi.findme_backend.exception.ResourceNotFoundException;
import com.dhi.findme_backend.mapper.UserMapper;
import com.dhi.findme_backend.repository.PasswordResetTokenRepository;
import com.dhi.findme_backend.repository.UserRepository;
import com.dhi.findme_backend.security.IJwtTokenProvider;
import com.dhi.findme_backend.service.AuthService;
import com.dhi.findme_backend.service.OAuth2UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthServiceImpl.class);
    private static final int DEFAULT_MAX_ADDRESSES = 4;

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final IJwtTokenProvider tokenProvider;
    private final OAuth2UserService oAuth2UserService;
    private final PasswordResetTokenRepository tokenRepository;

    public AuthServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager, IJwtTokenProvider tokenProvider,
                          OAuth2UserService oAuth2UserService, PasswordResetTokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.oAuth2UserService = oAuth2UserService;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public AuthResponse register(AuthRegisterRequest request) {
        // Vérifier si l'email existe déjà
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new BusinessException("EMAIL_ALREADY_EXISTS", "L'email est déjà associé à un compte");
        }

        // Créer l'utilisateur avec verified=false
        User user = new User();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setVerified(false);
        user.setRole("user");
        user.setAddressesCreatedCount(0);
        user.setMaxAddresses(DEFAULT_MAX_ADDRESSES);
        user.setPlan("free");
        user.setRegistrationDate(LocalDate.now());

        user = userRepository.save(user);

        // Générer le token JWT (l'utilisateur peut se connecter mais son email n'est pas vérifié)
        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), null);
        String token = tokenProvider.generateToken(authentication);
        
        LOGGER.info("Token généré pour l'utilisateur {}: {}", user.getEmail(), token != null ? token.substring(0, Math.min(20, token.length())) + "..." : "null");

        return new AuthResponse(token, userMapper.toUserResponse(user));
    }

    @Override
    public AuthResponse login(AuthLoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = tokenProvider.generateToken(authentication);

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("USER_NOT_FOUND", "Aucun compte avec cet email"));

        // Définir le rôle admin si l'email contient "admin"
        if (user.getEmail().contains("admin")) {
            user.setRole("admin");
            userRepository.save(user);
        }

        return new AuthResponse(token, userMapper.toUserResponse(user));
    }

    @Override
    public CompletableFuture<AuthResponse> loginWithGoogle(AuthGoogleRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            // Cette méthode est utilisée pour le flux avec token Google reçu du frontend
            // Pour l'instant, on garde la logique existante mais on pourrait l'améliorer
            // TODO: Implémenter la validation du token Google côté serveur
            
            // Pour l'instant, simulation - à remplacer par une vraie validation
            String email = "google.user@example.com";
            String firstName = "Google";
            String lastName = "User";

            User user = userRepository.findByEmail(email).orElseGet(() -> {
                User newUser = new User();
                newUser.setFirstName(firstName);
                newUser.setLastName(lastName);
                newUser.setEmail(email);
                newUser.setVerified(true);
                newUser.setRole("user");
                newUser.setAddressesCreatedCount(0);
                newUser.setMaxAddresses(DEFAULT_MAX_ADDRESSES);
                newUser.setPlan("free");
                newUser.setRegistrationDate(LocalDate.now());
                return userRepository.save(newUser);
            });

            // Générer le token JWT
            Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), null);
            String token = tokenProvider.generateToken(authentication);
            return new AuthResponse(token, userMapper.toUserResponse(user));
        });
    }

    public AuthResponse processOAuth2User(OAuth2User oAuth2User, String provider) {
        User user = oAuth2UserService.processOAuth2User(oAuth2User, provider);
        
        // Générer le token JWT
        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), null);
        String token = tokenProvider.generateToken(authentication);
        
        return new AuthResponse(token, userMapper.toUserResponse(user));
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        userRepository.findByEmail(request.email()).ifPresent(user -> {
            String token = UUID.randomUUID().toString();
            LocalDateTime expiryDate = LocalDateTime.now().plusHours(1); // Le token expire dans 1 heure

            PasswordResetToken resetToken = new PasswordResetToken(token, user, expiryDate);
            tokenRepository.save(resetToken);

            // Simuler l'envoi d'e-mail
            LOGGER.info("Password reset token for user {}: {}", user.getEmail(), token);
            // Dans une vraie application, vous enverriez un e-mail ici avec un lien comme :
            // String resetUrl = baseUrl + "/reset-password?token=" + token;
            // emailService.sendPasswordResetEmail(user.getEmail(), resetUrl);
        });
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("INVALID_TOKEN", "Le jeton de réinitialisation est invalide"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(resetToken);
            throw new BusinessException("EXPIRED_TOKEN", "Le jeton de réinitialisation a expiré");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        tokenRepository.delete(resetToken); // Le jeton est à usage unique
    }

    @Override
    public void logout() {
        // TODO: Implémenter la blacklist de tokens si nécessaire
    }

    @Override
    public void verifyEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("USER_NOT_FOUND", "Utilisateur non trouvé"));
        
        user.setVerified(true);
        userRepository.save(user);
    }

    @Override
    public void resetPasswordWithOtp(String email, String otp, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("USER_NOT_FOUND", "Utilisateur non trouvé"));
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}