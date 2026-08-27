package com.dhi.findme_backend.security;

import com.dhi.findme_backend.entity.User;
import com.dhi.findme_backend.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityUtilsTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SecurityUtils securityUtils;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("user@example.com");
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    private void authenticateAs(String email, String... authorities) {
        var granted = List.of(authorities).stream().map(SimpleGrantedAuthority::new).toList();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(email, null, granted));
    }

    @Test
    void getCurrentUserId_returnsIdOfAuthenticatedUser() {
        authenticateAs("user@example.com", "ROLE_USER");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        assertEquals(user.getId(), securityUtils.getCurrentUserId());
    }

    @Test
    void getCurrentUserId_whenNoAuthentication_throws() {
        SecurityContextHolder.clearContext();

        assertThrows(IllegalStateException.class, () -> securityUtils.getCurrentUserId());
    }

    @Test
    void getCurrentUserId_whenUserUnknown_throws() {
        authenticateAs("ghost@example.com", "ROLE_USER");
        when(userRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> securityUtils.getCurrentUserId());
    }

    @Test
    void getCurrentUserEmail_returnsAuthenticationName() {
        authenticateAs("user@example.com", "ROLE_USER");

        assertEquals("user@example.com", securityUtils.getCurrentUserEmail());
    }

    @Test
    void getCurrentUserEmail_whenNoAuthentication_throws() {
        SecurityContextHolder.clearContext();

        assertThrows(IllegalStateException.class, () -> securityUtils.getCurrentUserEmail());
    }

    @Test
    void isAdmin_isTrueOnlyForRoleAdmin() {
        authenticateAs("admin@example.com", "ROLE_ADMIN");
        assertTrue(securityUtils.isAdmin());

        authenticateAs("user@example.com", "ROLE_USER");
        assertFalse(securityUtils.isAdmin());
    }

    @Test
    void isAdmin_whenNoAuthentication_isFalse() {
        SecurityContextHolder.clearContext();

        assertFalse(securityUtils.isAdmin());
    }

    @Test
    void isAdmin_whenAnonymous_isFalse() {
        SecurityContextHolder.getContext().setAuthentication(new AnonymousAuthenticationToken(
                "key", "anonymous", List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))));

        assertFalse(securityUtils.isAdmin());
    }
}
