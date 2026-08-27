package com.dhi.findme_backend.security;

import com.dhi.findme_backend.entity.User;
import com.dhi.findme_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setEmail("security-test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setFirstName("Security");
        testUser.setLastName("Test");
        testUser.setRole("user");
    }

    @Test
    void testUserDetailsService_LoadUserByUsername_Success() {
        when(userRepository.findByEmail("security-test@example.com")).thenReturn(Optional.of(testUser));

        var userDetails = userDetailsService.loadUserByUsername("security-test@example.com");

        assertNotNull(userDetails);
        assertEquals("security-test@example.com", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
        verify(userRepository).findByEmail("security-test@example.com");
    }

    @Test
    void testUserDetailsService_LoadUserByUsername_NotFound() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(org.springframework.security.core.userdetails.UsernameNotFoundException.class,
            () -> userDetailsService.loadUserByUsername("nonexistent@example.com"));
        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    void testUserDetailsService_LoadUserByUsername_AdminRole() {
        testUser.setRole("admin");
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(testUser));

        var userDetails = userDetailsService.loadUserByUsername("admin@example.com");

        assertNotNull(userDetails);
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        verify(userRepository).findByEmail("admin@example.com");
    }

    @Test
    void testUserDetails_Authorities() {
        when(userRepository.findByEmail("security-test@example.com")).thenReturn(Optional.of(testUser));

        var userDetails = userDetailsService.loadUserByUsername("security-test@example.com");

        assertNotNull(userDetails.getAuthorities());
        assertEquals(1, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void testUserDetails_RoleIsUppercasedWhateverTheCasingStored() {
        testUser.setRole("AdMiN");
        when(userRepository.findByEmail("security-test@example.com")).thenReturn(Optional.of(testUser));

        var userDetails = userDetailsService.loadUserByUsername("security-test@example.com");

        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void testUserDetails_AccountFlags() {
        when(userRepository.findByEmail("security-test@example.com")).thenReturn(Optional.of(testUser));

        var userDetails = userDetailsService.loadUserByUsername("security-test@example.com");

        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());
    }

    @Test
    void testUserDetails_UnverifiedUserIsStillLoaded() {
        testUser.setVerified(false);
        when(userRepository.findByEmail("security-test@example.com")).thenReturn(Optional.of(testUser));

        var userDetails = userDetailsService.loadUserByUsername("security-test@example.com");

        assertNotNull(userDetails);
        assertEquals("security-test@example.com", userDetails.getUsername());
    }
}
