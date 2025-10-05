package br.com.jboard.orchestrator.controllers;

import br.com.jboard.orchestrator.models.User;
import br.com.jboard.orchestrator.models.dtos.AuthenticationDTO;
import br.com.jboard.orchestrator.models.dtos.ChangePasswordDTO;
import br.com.jboard.orchestrator.models.dtos.LoginResponseDTO;
import br.com.jboard.orchestrator.models.dtos.RegisterDTO;
import br.com.jboard.orchestrator.models.enums.RoleEnum;
import br.com.jboard.orchestrator.services.AuthenticationService;
import br.com.jboard.orchestrator.services.TokenService;
import br.com.jboard.orchestrator.utils.AuthUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {
    @Mock
    AuthenticationManager authenticationManager;
    @Mock
    AuthenticationService authenticationService;
    @Mock
    TokenService tokenService;
    @Mock
    AuthUtils authUtils;
    @Mock
    HttpServletRequest request;
    @InjectMocks
    AuthenticationController authenticationController;

    @Test
    void login_successfulAuthentication_returnsTokenResponse() {
        AuthenticationDTO authDto = new AuthenticationDTO();
        authDto.setUsername("testuser");
        authDto.setPassword("password");

        User user = new User("testuser", "password", RoleEnum.FREE);
        Authentication auth = mock(Authentication.class);
        String token = "jwt-token";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(user);
        when(tokenService.generateToken(user)).thenReturn(token);

        ResponseEntity<LoginResponseDTO> response = authenticationController.login(authDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(token, response.getBody().getToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenService).generateToken(user);
    }

    @Test
    void login_authenticationFailure_throwsException() {
        AuthenticationDTO authDto = new AuthenticationDTO();
        authDto.setUsername("testuser");
        authDto.setPassword("wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Authentication failed"));

        assertThrows(RuntimeException.class, () -> authenticationController.login(authDto));
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenService, never()).generateToken(any());
    }

    @Test
    void register_successfulRegistration_returnsOkResponse() {
        RegisterDTO registerDto = new RegisterDTO();
        registerDto.setUsername("newuser");
        registerDto.setPassword("password");

        doNothing().when(authenticationService).registerUser(registerDto);

        ResponseEntity response = authenticationController.register(registerDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authenticationService).registerUser(registerDto);
    }

    @Test
    void register_serviceThrowsException_propagatesException() {
        RegisterDTO registerDto = new RegisterDTO();
        registerDto.setUsername("existinguser");
        registerDto.setPassword("password");

        doThrow(new RuntimeException("User already exists")).when(authenticationService).registerUser(registerDto);

        assertThrows(RuntimeException.class, () -> authenticationController.register(registerDto));
        verify(authenticationService).registerUser(registerDto);
    }

    @Test
    void updatePassword_successfulUpdate_returnsOkResponse() {
        ChangePasswordDTO changePasswordDto = new ChangePasswordDTO();
        changePasswordDto.setOldPassword("oldpass");
        changePasswordDto.setNewPassword("newpass");

        String username = "testuser";

        when(authUtils.getUsernameFromRequest(request)).thenReturn(username);
        doNothing().when(authenticationService).updatePassword(changePasswordDto, username);

        ResponseEntity response = authenticationController.updatePassword(changePasswordDto, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authUtils).getUsernameFromRequest(request);
        verify(authenticationService).updatePassword(changePasswordDto, username);
    }

    @Test
    void updatePassword_invalidToken_throwsException() {
        ChangePasswordDTO changePasswordDto = new ChangePasswordDTO();
        changePasswordDto.setOldPassword("oldpass");
        changePasswordDto.setNewPassword("newpass");

        when(authUtils.getUsernameFromRequest(request)).thenThrow(new RuntimeException("Invalid token"));

        assertThrows(RuntimeException.class, () ->
            authenticationController.updatePassword(changePasswordDto, request));

        verify(authUtils).getUsernameFromRequest(request);
    }

    @Test
    void updatePassword_serviceThrowsException_propagatesException() {
        ChangePasswordDTO changePasswordDto = new ChangePasswordDTO();
        changePasswordDto.setOldPassword("wrongpass");
        changePasswordDto.setNewPassword("newpass");

        String username = "testuser";

        when(authUtils.getUsernameFromRequest(request)).thenReturn(username);
        doThrow(new RuntimeException("Old password incorrect")).when(authenticationService)
                .updatePassword(changePasswordDto, username);

        assertThrows(RuntimeException.class, () ->
            authenticationController.updatePassword(changePasswordDto, request));

        verify(authUtils).getUsernameFromRequest(request);
        verify(authenticationService).updatePassword(changePasswordDto, username);
    }

    @Test
    void deleteAccount_successfulDeletion_returnsOkResponse() {
        String username = "testuser";

        when(authUtils.getUsernameFromRequest(request)).thenReturn(username);
        doNothing().when(authenticationService).deleteAccount(username);

        ResponseEntity response = authenticationController.deleteAccount(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authUtils).getUsernameFromRequest(request);
        verify(authenticationService).deleteAccount(username);
    }

    @Test
    void deleteAccount_invalidToken_handlesGracefully() {
        when(authUtils.getUsernameFromRequest(request)).thenThrow(new RuntimeException("Invalid token"));

        assertThrows(RuntimeException.class, () -> authenticationController.deleteAccount(request));

        verify(authUtils).getUsernameFromRequest(request);
    }

    @Test
    void deleteAccount_serviceThrowsException_propagatesException() {
        String username = "testuser";

        when(authUtils.getUsernameFromRequest(request)).thenReturn(username);
        doThrow(new RuntimeException("User not found")).when(authenticationService).deleteAccount(username);

        assertThrows(RuntimeException.class, () -> authenticationController.deleteAccount(request));
        
        verify(authUtils).getUsernameFromRequest(request);
        verify(authenticationService).deleteAccount(username);
    }

    @Test
    void login_withEmptyCredentials_handlesGracefully() {
        AuthenticationDTO authDto = new AuthenticationDTO();
        authDto.setUsername("");
        authDto.setPassword("");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Empty credentials"));

        assertThrows(RuntimeException.class, () -> authenticationController.login(authDto));
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}
