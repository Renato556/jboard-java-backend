package br.com.jboard.orchestrator.controllers;

import br.com.jboard.orchestrator.models.dtos.ChangeRoleDTO;
import br.com.jboard.orchestrator.models.enums.RoleEnum;
import br.com.jboard.orchestrator.services.RoleService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleControllerTest {
    @Mock
    RoleService roleService;
    @Mock
    HttpServletRequest request;
    @InjectMocks
    RoleController roleController;

    @Test
    void updateRole_successfulUpdate_returnsOkResponse() {
        ChangeRoleDTO changeRoleDto = new ChangeRoleDTO();
        changeRoleDto.setUsername("testuser");
        changeRoleDto.setRole(RoleEnum.PREMIUM);

        String authHeader = "Basic dGVzdDp0ZXN0";
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        doNothing().when(roleService).updateRole(changeRoleDto, authHeader);

        ResponseEntity response = roleController.updateRole(changeRoleDto, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(roleService).updateRole(changeRoleDto, authHeader);
    }

    @Test
    void updateRole_serviceThrowsUnauthorizedException_propagatesException() {
        ChangeRoleDTO changeRoleDto = new ChangeRoleDTO();
        changeRoleDto.setUsername("testuser");
        changeRoleDto.setRole(RoleEnum.PREMIUM);

        String authHeader = "Basic invalid";
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        doThrow(new RuntimeException("Unauthorized")).when(roleService).updateRole(changeRoleDto, authHeader);

        assertThrows(RuntimeException.class, () -> roleController.updateRole(changeRoleDto, request));
        verify(roleService).updateRole(changeRoleDto, authHeader);
    }

    @Test
    void updateRole_nullAuthorizationHeader_passesNullToService() {
        ChangeRoleDTO changeRoleDto = new ChangeRoleDTO();
        changeRoleDto.setUsername("testuser");
        changeRoleDto.setRole(RoleEnum.FREE);

        when(request.getHeader("Authorization")).thenReturn(null);
        doThrow(new RuntimeException("Authorization required")).when(roleService).updateRole(changeRoleDto, null);

        assertThrows(RuntimeException.class, () -> roleController.updateRole(changeRoleDto, request));
        verify(roleService).updateRole(changeRoleDto, null);
    }

    @Test
    void updateRole_changingFromFreeToFree_handlesGracefully() {
        ChangeRoleDTO changeRoleDto = new ChangeRoleDTO();
        changeRoleDto.setUsername("testuser");
        changeRoleDto.setRole(RoleEnum.FREE);

        String authHeader = "Basic dGVzdDp0ZXN0";
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        doNothing().when(roleService).updateRole(changeRoleDto, authHeader);

        ResponseEntity response = roleController.updateRole(changeRoleDto, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(roleService).updateRole(changeRoleDto, authHeader);
    }

    @Test
    void updateRole_emptyUsername_passesToService() {
        ChangeRoleDTO changeRoleDto = new ChangeRoleDTO();
        changeRoleDto.setUsername("");
        changeRoleDto.setRole(RoleEnum.PREMIUM);

        String authHeader = "Basic dGVzdDp0ZXN0";
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        doThrow(new RuntimeException("User not found")).when(roleService).updateRole(changeRoleDto, authHeader);

        assertThrows(RuntimeException.class, () -> roleController.updateRole(changeRoleDto, request));
        verify(roleService).updateRole(changeRoleDto, authHeader);
    }
}
