package com.dhi.findme_backend.service.impl;

import com.dhi.findme_backend.dto.AdminUserCreateRequest;
import com.dhi.findme_backend.dto.AvatarUploadResponse;
import com.dhi.findme_backend.dto.UserPlanUpdateRequest;
import com.dhi.findme_backend.dto.UserResponse;
import com.dhi.findme_backend.dto.UserUpdateRequest;
import com.dhi.findme_backend.entity.User;
import com.dhi.findme_backend.exception.BusinessException;
import com.dhi.findme_backend.exception.ResourceNotFoundException;
import com.dhi.findme_backend.mapper.UserMapper;
import com.dhi.findme_backend.repository.UserRepository;
import com.dhi.findme_backend.repository.specification.UserSpecification;
import com.dhi.findme_backend.security.SecurityUtilsInterface;
import com.dhi.findme_backend.service.UserService;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    private static final long MAX_AVATAR_SIZE_BYTES = 5 * 1024 * 1024; // 5MB
    private static final int DEFAULT_MAX_ADDRESSES = 4;
    private static final int PREMIUM_MAX_ADDRESSES = 100;

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtilsInterface securityUtils;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder, SecurityUtilsInterface securityUtils) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.securityUtils = securityUtils;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        UUID currentUserId = securityUtils.getCurrentUserId();
        LOGGER.info("getCurrentUser appelé - ID utilisateur récupéré: {}", currentUserId);
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("USER_NOT_FOUND", "Utilisateur non trouvé"));
        LOGGER.info("Utilisateur trouvé: {} (email: {})", user.getId(), user.getEmail());
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse updateUser(UUID userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("USER_NOT_FOUND", "Utilisateur non trouvé"));

        if (request.firstName() != null) {
            user.setFirstName(request.firstName());
        }
        if (request.lastName() != null) {
            user.setLastName(request.lastName());
        }
        if (request.phone() != null) {
            user.setPhone(request.phone());
        }
        if (request.defaultLocation() != null) {
            user.setDefaultLocation(request.defaultLocation());
        }
        if (request.avatarUrl() != null) {
            user.setAvatarUrl(request.avatarUrl());
        }

        user = userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    @Override
    public void changePassword(UUID userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("USER_NOT_FOUND", "Utilisateur non trouvé"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("INVALID_OLD_PASSWORD", "L'ancien mot de passe est incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public AvatarUploadResponse uploadAvatar(UUID userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("USER_NOT_FOUND", "Utilisateur non trouvé"));

        // Valider le type de fichier
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.startsWith("image/"))) {
            throw new BusinessException("INVALID_FILE_TYPE", "Seuls les fichiers image sont autorisés");
        }

        // Valider la taille du fichier (max 5MB)
        if (file.getSize() > MAX_AVATAR_SIZE_BYTES) {
            throw new BusinessException("FILE_TOO_LARGE", "La taille du fichier ne doit pas dépasser 5MB");
        }

        try {
            // Créer le répertoire d'upload s'il n'existe pas
            Path uploadPath = Paths.get(uploadDir, "avatars");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Générer un nom de fichier unique
            String originalFilename = file.getOriginalFilename();
            String extension = FilenameUtils.getExtension(originalFilename);
            String newFilename = userId + "_" + System.currentTimeMillis() + "." + extension;
            Path filePath = uploadPath.resolve(newFilename);

            // Sauvegarder le fichier
            file.transferTo(filePath.toFile());

            // Construire l'URL publique
            String avatarUrl = baseUrl + "/uploads/avatars/" + newFilename;

            // Mettre à jour l'utilisateur
            user.setAvatarUrl(avatarUrl);
            userRepository.save(user);

            return new AvatarUploadResponse(avatarUrl, "Avatar uploadé avec succès");
        } catch (IOException e) {
            throw new BusinessException("UPLOAD_ERROR", "Erreur lors de l'upload de l'avatar: " + e.getMessage());
        }
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable, String search, String country, String plan) {
        Specification<User> spec = UserSpecification.filterBy(search, country, plan);
        Page<User> users = userRepository.findAll(spec, pageable);
        return users.map(userMapper::toUserResponse);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse createUserByAdmin(AdminUserCreateRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new BusinessException("EMAIL_ALREADY_EXISTS", "L'email est déjà associé à un compte");
        }

        User user = new User();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setVerified(true); // Les utilisateurs créés par un admin sont vérifiés par défaut

        user.setRole(StringUtils.hasText(request.role()) ? request.role() : "user");
        user.setPlan(StringUtils.hasText(request.plan()) ? request.plan() : "free");

        user.setAddressesCreatedCount(0);
        user.setMaxAddresses(DEFAULT_MAX_ADDRESSES); // Valeur par défaut, pourrait être ajustée en fonction du plan
        user.setRegistrationDate(LocalDate.now());

        user = userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("USER_NOT_FOUND", "Utilisateur non trouvé avec l'ID: " + userId);
        }
        userRepository.deleteById(userId);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateUserPlan(UUID userId, UserPlanUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("USER_NOT_FOUND", "Utilisateur non trouvé"));

        String newPlan = request.plan();
        user.setPlan(newPlan);

        // Mettre à jour les limites en fonction du plan
        if ("premium".equalsIgnoreCase(newPlan)) {
            user.setMaxAddresses(PREMIUM_MAX_ADDRESSES);
        } else {
            user.setMaxAddresses(DEFAULT_MAX_ADDRESSES);
        }

        user = userRepository.save(user);
        return userMapper.toUserResponse(user);
    }
}