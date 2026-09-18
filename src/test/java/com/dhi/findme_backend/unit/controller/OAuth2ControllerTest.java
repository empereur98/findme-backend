package com.dhi.findme_backend.unit.controller;

import com.dhi.findme_backend.controller.*;
import com.dhi.findme_backend.service.*;
import com.dhi.findme_backend.dto.*;
import com.dhi.findme_backend.entity.*;
import com.dhi.findme_backend.exception.*;
import com.dhi.findme_backend.security.*;
import com.dhi.findme_backend.dto.AuthResponse;
import com.dhi.findme_backend.dto.UserResponse;
import com.dhi.findme_backend.exception.GlobalExceptionHandler;
import com.dhi.findme_backend.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OAuth2ControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthServiceImpl authService;

    @InjectMocks
    private OAuth2Controller oAuth2Controller;

    private OAuth2User mockOAuth2User;

    @BeforeEach
    void setUp() {
        mockOAuth2User = mock(OAuth2User.class);

        HandlerMethodArgumentResolver authPrincipalResolver = new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                return parameter.hasParameterAnnotation(AuthenticationPrincipal.class);
            }

            @Override
            public Object resolveArgument(MethodParameter parameter,
                                          ModelAndViewContainer mavContainer,
                                          NativeWebRequest webRequest,
                                          WebDataBinderFactory binderFactory) {
                String authHeader = webRequest.getHeader("X-Simulate-OAuth2User");
                if ("present".equals(authHeader)) {
                    return mockOAuth2User;
                }
                return null;
            }
        };

        mockMvc = MockMvcBuilders.standaloneSetup(oAuth2Controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(authPrincipalResolver)
                .build();
    }

    @Test
    void googleCallback_whenOAuth2UserPresent_shouldReturnAuthResponse() throws Exception {
        UserResponse userResponse = new UserResponse(
                "oauth-user-id", "Jane", "Doe", "jane.doe@gmail.com",
                "https://avatar.url", null, null, true, "USER", 0, 5, "FREE", null
        );
        AuthResponse authResponse = new AuthResponse("oauth-jwt-token", userResponse);
        when(authService.processOAuth2User(eq(mockOAuth2User), eq("google"))).thenReturn(authResponse);

        mockMvc.perform(get("/api/auth/oauth2/callback/google")
                        .header("X-Simulate-OAuth2User", "present"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.data.token", is("oauth-jwt-token")))
                .andExpect(jsonPath("$.data.user.email", is("jane.doe@gmail.com")));

        verify(authService).processOAuth2User(mockOAuth2User, "google");
    }

    @Test
    void googleCallback_whenOAuth2UserNull_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/auth/oauth2/callback/google"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginPage_shouldReturnRedirectView() throws Exception {
        mockMvc.perform(get("/api/auth/oauth2/login"))
                .andExpect(status().isOk());
    }
}

