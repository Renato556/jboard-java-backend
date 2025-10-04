package br.com.jboard.orchestrator.services;

import br.com.jboard.orchestrator.clients.UserClient;
import br.com.jboard.orchestrator.models.User;
import br.com.jboard.orchestrator.models.dtos.ChangePasswordDTO;
import br.com.jboard.orchestrator.models.dtos.RegisterDTO;
import br.com.jboard.orchestrator.models.enums.RoleEnum;
import br.com.jboard.orchestrator.models.exceptions.ForbiddenException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    @Mock
    UserClient userClient;
    @InjectMocks
    AuthenticationService authenticationService;

    @Test
    void registerUser_successfulRegistration_callsUserClientWithEncryptedPassword() {
        RegisterDTO registerDto = new RegisterDTO();
        registerDto.setUsername("newuser");
        registerDto.setPassword("plainpassword");

        doNothing().when(userClient).registerUser(any(User.class));

        authenticationService.registerUser(registerDto);

        verify(userClient).registerUser(argThat(user ->
            user.getUsername().equals("newuser") &&
            user.getRole().equals(RoleEnum.FREE) &&
            !user.getPassword().equals("plainpassword")
        ));
    }

    @Test
    void registerUser_userClientThrowsException_propagatesException() {
        RegisterDTO registerDto = new RegisterDTO();
        registerDto.setUsername("existinguser");
        registerDto.setPassword("password");

        doThrow(new RuntimeException("User already exists")).when(userClient).registerUser(any(User.class));

        assertThrows(RuntimeException.class, () -> authenticationService.registerUser(registerDto));
        verify(userClient).registerUser(any(User.class));
    }

    @Test
    void updatePassword_correctOldPassword_updatesPassword() {
        String username = "testuser";
        String oldPassword = "oldpass";
        String newPassword = "newpass";
        String hashedOldPassword = new BCryptPasswordEncoder().encode(oldPassword);

        ChangePasswordDTO changePasswordDto = new ChangePasswordDTO();
        changePasswordDto.setOldPassword(oldPassword);
        changePasswordDto.setNewPassword(newPassword);

        User existingUser = new User(username, hashedOldPassword, RoleEnum.FREE);
        when(userClient.getUserByUsername(username)).thenReturn(existingUser);
        doNothing().when(userClient).updateUser(any(User.class));

        authenticationService.updatePassword(changePasswordDto, username);

        verify(userClient).getUserByUsername(username);
        verify(userClient).updateUser(argThat(user ->
            user.getUsername().equals(username) &&
            !user.getPassword().equals(hashedOldPassword) &&
            user.getRole().equals(RoleEnum.FREE)
        ));
    }

    @Test
    void updatePassword_incorrectOldPassword_throwsForbiddenException() {
        String username = "testuser";
        String oldPassword = "wrongpass";
        String newPassword = "newpass";
        String hashedOldPassword = new BCryptPasswordEncoder().encode("correctpass");

        ChangePasswordDTO changePasswordDto = new ChangePasswordDTO();
        changePasswordDto.setOldPassword(oldPassword);
        changePasswordDto.setNewPassword(newPassword);

        User existingUser = new User(username, hashedOldPassword, RoleEnum.FREE);
        when(userClient.getUserByUsername(username)).thenReturn(existingUser);

        ForbiddenException exception = assertThrows(ForbiddenException.class,
            () -> authenticationService.updatePassword(changePasswordDto, username));

        assertEquals("Old password does not match", exception.getMessage());
        verify(userClient).getUserByUsername(username);
        verify(userClient, never()).updateUser(any(User.class));
    }

    @Test
    void updatePassword_sameOldAndNewPassword_doesNotCallUpdate() {
        String username = "testuser";
        String password = "samepass";
        String hashedPassword = new BCryptPasswordEncoder().encode(password);

        ChangePasswordDTO changePasswordDto = new ChangePasswordDTO();
        changePasswordDto.setOldPassword(password);
        changePasswordDto.setNewPassword(password);

        User existingUser = new User(username, hashedPassword, RoleEnum.PREMIUM);
        when(userClient.getUserByUsername(username)).thenReturn(existingUser);

        authenticationService.updatePassword(changePasswordDto, username);

        verify(userClient).getUserByUsername(username);
        verify(userClient, never()).updateUser(any(User.class));
    }

    @Test
    void updatePassword_userClientThrowsException_propagatesException() {
        String username = "testuser";
        String oldPassword = "oldpass";
        String hashedOldPassword = new BCryptPasswordEncoder().encode(oldPassword);

        ChangePasswordDTO changePasswordDto = new ChangePasswordDTO();
        changePasswordDto.setOldPassword(oldPassword);
        changePasswordDto.setNewPassword("newpass");

        User existingUser = new User(username, hashedOldPassword, RoleEnum.FREE);
        when(userClient.getUserByUsername(username)).thenReturn(existingUser);
        doThrow(new RuntimeException("Update failed")).when(userClient).updateUser(any(User.class));

        assertThrows(RuntimeException.class,
            () -> authenticationService.updatePassword(changePasswordDto, username));

        verify(userClient).updateUser(any(User.class));
    }

    @Test
    void deleteAccount_successfulDeletion_callsUserClient() {
        String username = "testuser";
        doNothing().when(userClient).deleteAccount(username);

        authenticationService.deleteAccount(username);

        verify(userClient).deleteAccount(username);
    }

    @Test
    void deleteAccount_userClientThrowsException_propagatesException() {
        String username = "nonexistentuser";
        doThrow(new RuntimeException("User not found")).when(userClient).deleteAccount(username);

        assertThrows(RuntimeException.class, () -> authenticationService.deleteAccount(username));
        verify(userClient).deleteAccount(username);
    }

    @Test
    void registerUser_withNullPassword_handlesGracefully() {
        RegisterDTO registerDto = new RegisterDTO();
        registerDto.setUsername("testuser");
        registerDto.setPassword(null);

        assertThrows(IllegalArgumentException.class, () -> authenticationService.registerUser(registerDto));
    }

    @Test
    void updatePassword_getUserThrowsException_propagatesException() {
        String username = "testuser";
        ChangePasswordDTO changePasswordDto = new ChangePasswordDTO();
        changePasswordDto.setOldPassword("oldpass");
        changePasswordDto.setNewPassword("newpass");

        when(userClient.getUserByUsername(username)).thenThrow(new RuntimeException("User not found"));

        assertThrows(RuntimeException.class,
            () -> authenticationService.updatePassword(changePasswordDto, username));

        verify(userClient).getUserByUsername(username);
        verify(userClient, never()).updateUser(any(User.class));
    }
}
