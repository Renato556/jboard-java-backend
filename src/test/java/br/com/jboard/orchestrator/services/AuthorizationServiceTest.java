package br.com.jboard.orchestrator.services;

import br.com.jboard.orchestrator.clients.UserClient;
import br.com.jboard.orchestrator.models.User;
import br.com.jboard.orchestrator.models.enums.RoleEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {
    @Mock
    UserClient userClient;
    @InjectMocks
    AuthorizationService authorizationService;

    @Test
    void loadUserByUsername_existingUser_returnsUserDetails() {
        String username = "testuser";
        User user = new User(username, "password", RoleEnum.FREE);

        when(userClient.getUserByUsername(username)).thenReturn(user);

        UserDetails userDetails = authorizationService.loadUserByUsername(username);

        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        verify(userClient).getUserByUsername(username);
    }

    @Test
    void loadUserByUsername_premiumUser_returnsUserWithCorrectAuthorities() {
        String username = "premiumuser";
        User user = new User(username, "password", RoleEnum.PREMIUM);

        when(userClient.getUserByUsername(username)).thenReturn(user);

        UserDetails userDetails = authorizationService.loadUserByUsername(username);

        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals(2, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_PREMIUM")));
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_FREE")));
    }

    @Test
    void loadUserByUsername_freeUser_returnsUserWithFreeAuthority() {
        String username = "freeuser";
        User user = new User(username, "password", RoleEnum.FREE);

        when(userClient.getUserByUsername(username)).thenReturn(user);

        UserDetails userDetails = authorizationService.loadUserByUsername(username);

        assertNotNull(userDetails);
        assertEquals(1, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_FREE")));
        assertFalse(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_PREMIUM")));
    }

    @Test
    void loadUserByUsername_userClientReturnsNull_returnsNull() {
        String username = "nonexistentuser";

        when(userClient.getUserByUsername(username)).thenReturn(null);

        UserDetails userDetails = authorizationService.loadUserByUsername(username);

        assertNull(userDetails);
        verify(userClient).getUserByUsername(username);
    }

    @Test
    void loadUserByUsername_userClientThrowsException_propagatesException() {
        String username = "erroruser";

        when(userClient.getUserByUsername(username))
                .thenThrow(new RuntimeException("Database connection error"));

        assertThrows(RuntimeException.class,
                () -> authorizationService.loadUserByUsername(username));
        verify(userClient).getUserByUsername(username);
    }

    @Test
    void loadUserByUsername_emptyUsername_callsUserClient() {
        String username = "";
        User user = new User(username, "password", RoleEnum.FREE);

        when(userClient.getUserByUsername(username)).thenReturn(user);

        UserDetails userDetails = authorizationService.loadUserByUsername(username);

        assertNotNull(userDetails);
        assertEquals("", userDetails.getUsername());
        verify(userClient).getUserByUsername(username);
    }

    @Test
    void loadUserByUsername_nullUsername_callsUserClient() {
        when(userClient.getUserByUsername(null)).thenReturn(null);

        UserDetails userDetails = authorizationService.loadUserByUsername(null);

        assertNull(userDetails);
        verify(userClient).getUserByUsername(null);
    }

    @Test
    void loadUserByUsername_userWithSpecialCharacters_handlesCorrectly() {
        String username = "test@user.com";
        User user = new User(username, "password", RoleEnum.PREMIUM);

        when(userClient.getUserByUsername(username)).thenReturn(user);

        UserDetails userDetails = authorizationService.loadUserByUsername(username);

        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        verify(userClient).getUserByUsername(username);
    }
}
