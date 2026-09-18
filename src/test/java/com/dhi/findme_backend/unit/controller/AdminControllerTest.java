package com.dhi.findme_backend.unit.controller;

import com.dhi.findme_backend.controller.*;
import com.dhi.findme_backend.service.*;
import com.dhi.findme_backend.dto.*;
import com.dhi.findme_backend.entity.*;
import com.dhi.findme_backend.exception.*;
import com.dhi.findme_backend.security.*;
import com.dhi.findme_backend.dto.AdminStatsResponse;
import com.dhi.findme_backend.exception.GlobalExceptionHandler;
import com.dhi.findme_backend.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getStats_shouldReturnAdminStats() throws Exception {
        AdminStatsResponse response = new AdminStatsResponse(150L, 25L, 3L, BigDecimal.valueOf(50000));
        when(adminService.getStats()).thenReturn(response);

        mockMvc.perform(get("/api/admin/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.data.totalUsers", is(150)))
                .andExpect(jsonPath("$.data.newUsersLast30Days", is(25)))
                .andExpect(jsonPath("$.data.openSupportTickets", is(3)))
                .andExpect(jsonPath("$.data.totalRevenue", is(50000)));

        verify(adminService).getStats();
    }
}

