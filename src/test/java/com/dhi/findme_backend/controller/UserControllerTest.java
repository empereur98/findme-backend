package com.dhi.findme_backend.controller;

import com.dhi.findme_backend.dto.*;
import com.dhi.findme_backend.exception.BusinessException;
import com.dhi.findme_backend.security.SecurityUtilsInterface;
import com.dhi.findme_backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private SecurityUtilsInterface securityUtils;

    @InjectMocks
    private UserController userController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private UUID userId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new com.dhi.findme_backend.exception.GlobalExceptionHandler())
                .build();

        userId = UUID.randomUUID();
        lenient().when(securityUtils.getCurrentUserId()).thenReturn(userId);
    }

    @Test
    void getCurrentUser_whenAuthenticated_shouldReturnUserData() throws Exception {
        UserResponse response = new UserResponse(userId.toString(), "Regular", "User", "user@example.com", null, null, null, true, "USER", 0, 5, "free", java.time.LocalDate.now());
        when(userService.getCurrentUser()).thenReturn(response);

        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email", is("user@example.com")));
    }

    @Test
    void updateUser_whenAuthenticated_shouldUpdateAndReturnUserData() throws Exception {
        UserUpdateRequest request = new UserUpdateRequest("Updated", "Name", null, null, null);
        UserResponse response = new UserResponse(userId.toString(), "Updated", "Name", "user@example.com", null, null, null, true, "USER", 0, 5, "free", java.time.LocalDate.now());
        when(userService.updateUser(eq(userId), any(UserUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/api/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.firstName", is("Updated")));
    }

    @Test
    void changePassword_withValidCredentials_shouldSucceed() throws Exception {
        PasswordChangeRequest request = new PasswordChangeRequest("password", "newPassword");
        doNothing().when(userService).changePassword(userId, "password", "newPassword");

        mockMvc.perform(put("/api/users/me/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void changePassword_withInvalidOldPassword_shouldReturnBadRequest() throws Exception {
        PasswordChangeRequest request = new PasswordChangeRequest("wrong-password", "newPassword");
        doThrow(new BusinessException("INVALID_OLD_PASSWORD", "Invalid old password"))
                .when(userService).changePassword(userId, "wrong-password", "newPassword");

        mockMvc.perform(put("/api/users/me/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("INVALID_OLD_PASSWORD")));
    }

    @Test
    void createUserByAdmin_shouldSucceed() throws Exception {
        AdminUserCreateRequest request = new AdminUserCreateRequest("New", "User", "new.user@example.com", "password123", "USER", "free");
        UserResponse response = new UserResponse(UUID.randomUUID().toString(), "New", "User", "new.user@example.com", null, null, null, true, "USER", 0, 5, "free", java.time.LocalDate.now());
        when(userService.createUserByAdmin(any(AdminUserCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.email", is("new.user@example.com")));
    }

    @Test
    void deleteUser_shouldSucceed() throws Exception {
        doNothing().when(userService).deleteUser(userId);
        mockMvc.perform(delete("/api/users/" + userId))
                .andExpect(status().isOk());
    }

    @Test
    void updateUserPlan_shouldSucceed() throws Exception {
        UserPlanUpdateRequest request = new UserPlanUpdateRequest("premium");
        UserResponse response = new UserResponse(userId.toString(), "Regular", "User", "user@example.com", null, null, null, true, "USER", 0, 5, "premium", java.time.LocalDate.now());
        when(userService.updateUserPlan(eq(userId), any(UserPlanUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/users/" + userId + "/plan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.plan", is("premium")));
    }
}