package br.com.jboard.orchestrator.controllers;

import br.com.jboard.orchestrator.models.dtos.SkillDTO;
import br.com.jboard.orchestrator.models.dtos.SkillResponseDTO;
import br.com.jboard.orchestrator.services.SkillService;
import br.com.jboard.orchestrator.utils.AuthUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillControllerTest {

    @Mock
    private SkillService skillService;

    @Mock
    private AuthUtils authUtils;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private SkillController skillController;

    private final String testUsername = "testuser";

    @Test
    void getAllSkills_ShouldReturnSkills_WhenUserIsAuthenticated() {
        SkillResponseDTO responseDTO = new SkillResponseDTO(
                List.of("java", "python"),
                new br.com.jboard.orchestrator.models.dtos.MetaDTO(2)
        );

        when(authUtils.getUsernameFromRequest(any(HttpServletRequest.class))).thenReturn(testUsername);
        when(skillService.getAllSkills(testUsername)).thenReturn(responseDTO);

        ResponseEntity<SkillResponseDTO> result = skillController.getAllSkills(request);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(responseDTO, result.getBody());
        verify(authUtils).getUsernameFromRequest(any(HttpServletRequest.class));
        verify(skillService).getAllSkills(testUsername);
    }

    @Test
    void getAllSkills_ShouldReturnEmptyList_WhenUserHasNoSkills() {
        SkillResponseDTO responseDTO = new SkillResponseDTO(
                List.of(),
                new br.com.jboard.orchestrator.models.dtos.MetaDTO(0)
        );

        when(authUtils.getUsernameFromRequest(any(HttpServletRequest.class))).thenReturn(testUsername);
        when(skillService.getAllSkills(testUsername)).thenReturn(responseDTO);

        ResponseEntity<SkillResponseDTO> result = skillController.getAllSkills(request);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(responseDTO, result.getBody());
        verify(authUtils).getUsernameFromRequest(any(HttpServletRequest.class));
        verify(skillService).getAllSkills(testUsername);
    }

    @Test
    void addSkill_ShouldReturnOk_WhenSkillIsValid() {
        SkillDTO skillDTO = new SkillDTO("java");
        when(authUtils.getUsernameFromRequest(any(HttpServletRequest.class))).thenReturn(testUsername);

        ResponseEntity<Void> result = skillController.addSkill(skillDTO, request);

        assertEquals(200, result.getStatusCode().value());
        verify(authUtils).getUsernameFromRequest(any(HttpServletRequest.class));
        verify(skillService).addSkill(eq(skillDTO), eq(testUsername));
    }

    @Test
    void removeSkill_ShouldReturnOk_WhenSkillIsValid() {
        SkillDTO skillDTO = new SkillDTO("java");
        when(authUtils.getUsernameFromRequest(any(HttpServletRequest.class))).thenReturn(testUsername);

        ResponseEntity<Void> result = skillController.removeSkill(skillDTO, request);

        assertEquals(200, result.getStatusCode().value());
        verify(authUtils).getUsernameFromRequest(any(HttpServletRequest.class));
        verify(skillService).removeSkill(eq(skillDTO), eq(testUsername));
    }

    @Test
    void deleteAllSkills_ShouldReturnOk_WhenUserIsAuthenticated() {
        when(authUtils.getUsernameFromRequest(any(HttpServletRequest.class))).thenReturn(testUsername);

        ResponseEntity<Void> result = skillController.deleteAllSkills(request);

        assertEquals(200, result.getStatusCode().value());
        verify(authUtils).getUsernameFromRequest(any(HttpServletRequest.class));
        verify(skillService).deleteAllSkills(testUsername);
    }
}
