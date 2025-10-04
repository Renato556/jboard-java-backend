package br.com.jboard.orchestrator.services;

import br.com.jboard.orchestrator.clients.UserClient;
import br.com.jboard.orchestrator.models.User;
import br.com.jboard.orchestrator.models.dtos.ChangeRoleDTO;
import br.com.jboard.orchestrator.models.enums.RoleEnum;
import br.com.jboard.orchestrator.models.exceptions.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {
    @Mock
    UserClient userClient;
    @InjectMocks
    RoleService roleService;

    @BeforeEach
    void setUp() throws Exception {
        Field adminCredentialsField = RoleService.class.getDeclaredField("adminCredentials");
        adminCredentialsField.setAccessible(true);
        String encodedCredentials = Base64.getEncoder().encodeToString("admin:password".getBytes());
        adminCredentialsField.set(roleService, encodedCredentials);
    }

    @Test
    void updateRole_validCredentialsAndDifferentRole_updatesUserRole() {
        String username = "testuser";
        ChangeRoleDTO changeRoleDto = new ChangeRoleDTO();
        changeRoleDto.setUsername(username);
        changeRoleDto.setRole(RoleEnum.PREMIUM);

        User user = new User(username, "password", RoleEnum.FREE);
        String validAuthHeader = "Basic " + Base64.getEncoder().encodeToString("admin:password".getBytes());

        when(userClient.getUserByUsername(username)).thenReturn(user);
        doNothing().when(userClient).updateUser(any(User.class));

        roleService.updateRole(changeRoleDto, validAuthHeader);

        verify(userClient).getUserByUsername(username);
        verify(userClient).updateUser(argThat(u -> u.getRole().equals(RoleEnum.PREMIUM)));
    }

    @Test
    void updateRole_validCredentialsAndSameRole_doesNotUpdateUser() {
        String username = "testuser";
        ChangeRoleDTO changeRoleDto = new ChangeRoleDTO();
        changeRoleDto.setUsername(username);
        changeRoleDto.setRole(RoleEnum.FREE);

        User user = new User(username, "password", RoleEnum.FREE);
        String validAuthHeader = "Basic " + Base64.getEncoder().encodeToString("admin:password".getBytes());

        when(userClient.getUserByUsername(username)).thenReturn(user);

        roleService.updateRole(changeRoleDto, validAuthHeader);

        verify(userClient).getUserByUsername(username);
        verify(userClient, never()).updateUser(any(User.class));
    }

    @Test
    void updateRole_nullAuthorizationHeader_throwsUnauthorizedException() {
        ChangeRoleDTO changeRoleDto = new ChangeRoleDTO();
        changeRoleDto.setUsername("testuser");
        changeRoleDto.setRole(RoleEnum.PREMIUM);

        UnauthorizedException exception = assertThrows(UnauthorizedException.class,
            () -> roleService.updateRole(changeRoleDto, null));

        assertEquals("Basic authentication required", exception.getMessage());
        verify(userClient, never()).getUserByUsername(any());
        verify(userClient, never()).updateUser(any());
    }

    @Test
    void updateRole_invalidAuthorizationFormat_throwsUnauthorizedException() {
        ChangeRoleDTO changeRoleDto = new ChangeRoleDTO();
        changeRoleDto.setUsername("testuser");
        changeRoleDto.setRole(RoleEnum.PREMIUM);

        String invalidAuthHeader = "Bearer token";

        UnauthorizedException exception = assertThrows(UnauthorizedException.class,
            () -> roleService.updateRole(changeRoleDto, invalidAuthHeader));

        assertEquals("Basic authentication required", exception.getMessage());
        verify(userClient, never()).getUserByUsername(any());
    }

    @Test
    void updateRole_invalidCredentials_throwsUnauthorizedException() {
        ChangeRoleDTO changeRoleDto = new ChangeRoleDTO();
        changeRoleDto.setUsername("testuser");
        changeRoleDto.setRole(RoleEnum.PREMIUM);

        String invalidAuthHeader = "Basic " + Base64.getEncoder().encodeToString("wrong:credentials".getBytes());

        UnauthorizedException exception = assertThrows(UnauthorizedException.class,
            () -> roleService.updateRole(changeRoleDto, invalidAuthHeader));

        assertEquals("Invalid admin credentials", exception.getMessage());
        verify(userClient, never()).getUserByUsername(any());
    }

    @Test
    void updateRole_emptyAuthorizationHeader_throwsUnauthorizedException() {
        ChangeRoleDTO changeRoleDto = new ChangeRoleDTO();
        changeRoleDto.setUsername("testuser");
        changeRoleDto.setRole(RoleEnum.PREMIUM);

        UnauthorizedException exception = assertThrows(UnauthorizedException.class,
            () -> roleService.updateRole(changeRoleDto, ""));

        assertEquals("Basic authentication required", exception.getMessage());
        verify(userClient, never()).getUserByUsername(any());
    }

    @Test
    void updateRole_userClientThrowsException_propagatesException() {
        String username = "nonexistentuser";
        ChangeRoleDTO changeRoleDto = new ChangeRoleDTO();
        changeRoleDto.setUsername(username);
        changeRoleDto.setRole(RoleEnum.PREMIUM);

        String validAuthHeader = "Basic " + Base64.getEncoder().encodeToString("admin:password".getBytes());

        when(userClient.getUserByUsername(username)).thenThrow(new RuntimeException("User not found"));

        assertThrows(RuntimeException.class, () -> roleService.updateRole(changeRoleDto, validAuthHeader));
        verify(userClient).getUserByUsername(username);
        verify(userClient, never()).updateUser(any());
    }

    @Test
    void updateRole_updateUserThrowsException_propagatesException() {
        String username = "testuser";
        ChangeRoleDTO changeRoleDto = new ChangeRoleDTO();
        changeRoleDto.setUsername(username);
        changeRoleDto.setRole(RoleEnum.PREMIUM);

        User user = new User(username, "password", RoleEnum.FREE);
        String validAuthHeader = "Basic " + Base64.getEncoder().encodeToString("admin:password".getBytes());

        when(userClient.getUserByUsername(username)).thenReturn(user);
        doThrow(new RuntimeException("Update failed")).when(userClient).updateUser(any(User.class));

        assertThrows(RuntimeException.class, () -> roleService.updateRole(changeRoleDto, validAuthHeader));
        verify(userClient).updateUser(any(User.class));
    }

    @Test
    void updateRole_changingFromPremiumToFree_updatesSuccessfully() {
        String username = "premiumuser";
        ChangeRoleDTO changeRoleDto = new ChangeRoleDTO();
        changeRoleDto.setUsername(username);
        changeRoleDto.setRole(RoleEnum.FREE);

        User user = new User(username, "password", RoleEnum.PREMIUM);
        String validAuthHeader = "Basic " + Base64.getEncoder().encodeToString("admin:password".getBytes());

        when(userClient.getUserByUsername(username)).thenReturn(user);
        doNothing().when(userClient).updateUser(any(User.class));

        roleService.updateRole(changeRoleDto, validAuthHeader);

        verify(userClient).getUserByUsername(username);
        verify(userClient).updateUser(argThat(u -> u.getRole().equals(RoleEnum.FREE)));
    }

    @Test
    void updateRole_malformedBase64Credentials_throwsUnauthorizedException() {
        ChangeRoleDTO changeRoleDto = new ChangeRoleDTO();
        changeRoleDto.setUsername("testuser");
        changeRoleDto.setRole(RoleEnum.PREMIUM);

        String malformedAuthHeader = "Basic invalidbase64!@#";

        assertThrows(Exception.class, () -> roleService.updateRole(changeRoleDto, malformedAuthHeader));
        verify(userClient, never()).getUserByUsername(any());
    }
}
