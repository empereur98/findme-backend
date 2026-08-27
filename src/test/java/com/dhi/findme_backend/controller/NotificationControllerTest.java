package com.dhi.findme_backend.controller;

import com.dhi.findme_backend.dto.NotificationResponse;
import com.dhi.findme_backend.exception.ResourceNotFoundException;
import com.dhi.findme_backend.security.SecurityUtilsInterface;
import com.dhi.findme_backend.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class NotificationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private NotificationService notificationService;

    @Mock
    private SecurityUtilsInterface securityUtils;

    @InjectMocks
    private NotificationController notificationController;

    private UUID userId;
    private UUID notificationId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(notificationController)
                .setControllerAdvice(new com.dhi.findme_backend.exception.GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        userId = UUID.randomUUID();
        notificationId = UUID.randomUUID();
        when(securityUtils.getCurrentUserId()).thenReturn(userId);
        when(securityUtils.isAdmin()).thenReturn(false);
    }

    @Test
    void getNotifications_whenAuthenticated_shouldReturnPageOfNotifications() throws Exception {
        NotificationResponse response = new NotificationResponse(notificationId, "Title", "Message", "INFO", false, null, null);
        lenient().when(notificationService.getNotifications(eq(userId), any(Pageable.class), any(Boolean.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(response), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk());
    }

    @Test
    void markNotificationAsRead_whenOwner_shouldSucceed() throws Exception {
        NotificationResponse response = new NotificationResponse(notificationId, "Title", "Message", "INFO", true, null, null);
        when(notificationService.markAsRead(notificationId, userId)).thenReturn(response);

        mockMvc.perform(patch("/api/notifications/" + notificationId + "/read"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.read", is(true)));
    }

    @Test
    void markNotificationAsRead_whenNotOwner_shouldReturnNotFound() throws Exception {
        when(notificationService.markAsRead(notificationId, userId))
                .thenThrow(new ResourceNotFoundException("NOTIFICATION_NOT_FOUND", "Notification not found"));

        mockMvc.perform(patch("/api/notifications/" + notificationId + "/read"))
                .andExpect(status().isNotFound());
    }

    @Test
    void markAllNotificationsAsRead_whenAuthenticated_shouldSucceed() throws Exception {
        doNothing().when(notificationService).markAllAsRead(userId);

        mockMvc.perform(patch("/api/notifications/read-all"))
                .andExpect(status().isOk());
    }
}