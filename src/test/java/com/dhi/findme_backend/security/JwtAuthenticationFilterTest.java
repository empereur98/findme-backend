package com.dhi.findme_backend.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private IJwtTokenProvider tokenProvider;

    @Mock
    private UserDetailsService userDetailsService;

    private JwtAuthenticationFilter filter;

    private final UserDetails userDetails = User.builder()
            .username("user@example.com")
            .password("encoded")
            .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
            .build();

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter(tokenProvider, userDetailsService);
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    private MockHttpServletRequest request(String uri, String authorizationHeader) {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", uri);
        request.setRequestURI(uri);
        if (authorizationHeader != null) {
            request.addHeader("Authorization", authorizationHeader);
        }
        return request;
    }

    @Test
    void doFilter_withValidBearerToken_authenticatesUser() throws Exception {
        when(tokenProvider.validateToken("valid-token")).thenReturn(true);
        when(tokenProvider.getUsernameFromJWT("valid-token")).thenReturn("user@example.com");
        when(userDetailsService.loadUserByUsername("user@example.com")).thenReturn(userDetails);

        MockFilterChain chain = new MockFilterChain();
        filter.doFilter(request("/api/users/me", "Bearer valid-token"), new MockHttpServletResponse(), chain);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(userDetails, authentication.getPrincipal());
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
        assertNotNull(chain.getRequest(), "la requête doit continuer dans la chaîne");
    }

    @Test
    void doFilter_withInvalidToken_leavesContextEmptyButContinuesChain() throws Exception {
        when(tokenProvider.validateToken("bad-token")).thenReturn(false);

        MockFilterChain chain = new MockFilterChain();
        filter.doFilter(request("/api/users/me", "Bearer bad-token"), new MockHttpServletResponse(), chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertNotNull(chain.getRequest());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }

    @Test
    void doFilter_withoutAuthorizationHeader_leavesContextEmpty() throws Exception {
        MockFilterChain chain = new MockFilterChain();
        filter.doFilter(request("/api/users/me", null), new MockHttpServletResponse(), chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(tokenProvider, never()).validateToken(anyString());
    }

    @Test
    void doFilter_withNonBearerHeader_ignoresIt() throws Exception {
        MockFilterChain chain = new MockFilterChain();
        filter.doFilter(request("/api/users/me", "Basic dXNlcjpwYXNz"), new MockHttpServletResponse(), chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(tokenProvider, never()).validateToken(anyString());
    }

    @Test
    void doFilter_whenUserDetailsLookupFails_swallowsErrorAndContinues() throws Exception {
        when(tokenProvider.validateToken("valid-token")).thenReturn(true);
        when(tokenProvider.getUsernameFromJWT("valid-token")).thenReturn("user@example.com");
        when(userDetailsService.loadUserByUsername("user@example.com"))
                .thenThrow(new org.springframework.security.core.userdetails.UsernameNotFoundException("nope"));

        MockFilterChain chain = new MockFilterChain();
        filter.doFilter(request("/api/users/me", "Bearer valid-token"), new MockHttpServletResponse(), chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertNotNull(chain.getRequest());
    }

    @Test
    void shouldNotFilter_publicEndpointsAreSkipped() {
        assertTrue(filter.shouldNotFilter(request("/api/auth/login", null)));
        assertTrue(filter.shouldNotFilter(request("/api/geo/countries", null)));
        assertTrue(filter.shouldNotFilter(request("/swagger-ui/index.html", null)));
        assertTrue(filter.shouldNotFilter(request("/v3/api-docs/swagger-config", null)));
        assertTrue(filter.shouldNotFilter(request("/actuator/health", null)));
        assertFalse(filter.shouldNotFilter(request("/api/users/me", null)));
    }
}
