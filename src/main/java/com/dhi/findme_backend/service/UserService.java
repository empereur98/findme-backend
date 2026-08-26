package com.dhi.findme_backend.service;

import com.dhi.findme_backend.dto.AdminUserCreateRequest;
import com.dhi.findme_backend.dto.AvatarUploadResponse;
import com.dhi.findme_backend.dto.UserPlanUpdateRequest;
import com.dhi.findme_backend.dto.UserResponse;
import com.dhi.findme_backend.dto.UserUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface UserService {

    UserResponse getCurrentUser();

    UserResponse updateUser(UUID userId, UserUpdateRequest request);

    void changePassword(UUID userId, String oldPassword, String newPassword);

    AvatarUploadResponse uploadAvatar(UUID userId, MultipartFile file);

    Page<UserResponse> getAllUsers(Pageable pageable, String search, String country, String plan);

    UserResponse createUserByAdmin(AdminUserCreateRequest request);

    void deleteUser(UUID userId);

    UserResponse updateUserPlan(UUID userId, UserPlanUpdateRequest request);
}