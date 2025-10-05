package br.com.jboard.orchestrator.services;

import br.com.jboard.orchestrator.clients.SkillClient;
import br.com.jboard.orchestrator.models.Skill;
import br.com.jboard.orchestrator.models.dtos.SkillDTO;
import br.com.jboard.orchestrator.models.dtos.SkillResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @Mock
    private SkillClient skillClient;

    @InjectMocks
    private SkillService skillService;

    private final String testUsername = "testuser";

    @Test
    void getAllSkills_ShouldReturnSkillResponseDTO_WhenSkillsExist() {
        List<Skill> mockSkills = List.of(
                new Skill(testUsername, "java"),
                new Skill(testUsername, "python")
        );
        when(skillClient.getAllSkills(testUsername)).thenReturn(mockSkills);

        SkillResponseDTO result = skillService.getAllSkills(testUsername);

        assertNotNull(result);
        assertEquals(2, result.getSkills().size());
        assertTrue(result.getSkills().contains("java"));
        assertTrue(result.getSkills().contains("python"));
        assertEquals(2, result.getMeta().getTotalRecords());
        verify(skillClient).getAllSkills(testUsername);
    }

    @Test
    void getAllSkills_ShouldReturnEmptyList_WhenNoSkillsExist() {
        when(skillClient.getAllSkills(testUsername)).thenReturn(List.of());

        SkillResponseDTO result = skillService.getAllSkills(testUsername);

        assertNotNull(result);
        assertTrue(result.getSkills().isEmpty());
        assertEquals(0, result.getMeta().getTotalRecords());
        verify(skillClient).getAllSkills(testUsername);
    }

    @Test
    void getAllSkills_ShouldReturnEmptyList_When404NotFound() {
        when(skillClient.getAllSkills(testUsername)).thenThrow(HttpClientErrorException.NotFound.class);

        SkillResponseDTO result = skillService.getAllSkills(testUsername);

        assertNotNull(result);
        assertTrue(result.getSkills().isEmpty());
        assertEquals(0, result.getMeta().getTotalRecords());
        verify(skillClient).getAllSkills(testUsername);
    }

    @Test
    void getAllSkills_ShouldThrowException_WhenOtherErrorOccurs() {
        when(skillClient.getAllSkills(testUsername)).thenThrow(new RuntimeException("Database error"));
 
        assertThrows(RuntimeException.class, () -> skillService.getAllSkills(testUsername));
        verify(skillClient).getAllSkills(testUsername);
    }

    @Test
    void addSkill_ShouldNormalizeSkillAndCallClient_WhenSkillIsValid() {
        SkillDTO skillDTO = new SkillDTO("  JAVA  ");
        Skill expectedSkill = new Skill(testUsername, "java");

        assertDoesNotThrow(() -> skillService.addSkill(skillDTO, testUsername));

        verify(skillClient).addSkill(expectedSkill);
    }

    @Test
    void addSkill_ShouldThrowException_WhenSkillIsNull() {
        SkillDTO skillDTO = new SkillDTO(null);
 
        assertThrows(IllegalArgumentException.class, () -> skillService.addSkill(skillDTO, testUsername));
        verify(skillClient, never()).addSkill(any());
    }

    @Test
    void addSkill_ShouldThrowException_WhenSkillIsEmpty() {
        SkillDTO skillDTO = new SkillDTO("   ");
 
        assertThrows(IllegalArgumentException.class, () -> skillService.addSkill(skillDTO, testUsername));
        verify(skillClient, never()).addSkill(any());
    }

    @Test
    void addSkill_ShouldThrowException_WhenClientFails() {
        SkillDTO skillDTO = new SkillDTO("java");
        doThrow(new RuntimeException("Client error")).when(skillClient).addSkill(any());
 
        assertThrows(RuntimeException.class, () -> skillService.addSkill(skillDTO, testUsername));
        verify(skillClient).addSkill(any());
    }

    @Test
    void removeSkill_ShouldNormalizeSkillAndCallClient_WhenSkillIsValid() {
        SkillDTO skillDTO = new SkillDTO("  PYTHON  ");
        Skill expectedSkill = new Skill(testUsername, "python");

        assertDoesNotThrow(() -> skillService.removeSkill(skillDTO, testUsername));

        verify(skillClient).removeSkill(expectedSkill);
    }

    @Test
    void removeSkill_ShouldThrowException_WhenSkillIsNull() {
        SkillDTO skillDTO = new SkillDTO(null);
 
        assertThrows(IllegalArgumentException.class, () -> skillService.removeSkill(skillDTO, testUsername));
        verify(skillClient, never()).removeSkill(any());
    }

    @Test
    void removeSkill_ShouldThrowException_WhenSkillIsEmpty() {
        SkillDTO skillDTO = new SkillDTO("");
 
        assertThrows(IllegalArgumentException.class, () -> skillService.removeSkill(skillDTO, testUsername));
        verify(skillClient, never()).removeSkill(any());
    }

    @Test
    void removeSkill_ShouldThrowException_WhenClientFails() {
        SkillDTO skillDTO = new SkillDTO("java");
        doThrow(new RuntimeException("Client error")).when(skillClient).removeSkill(any());
 
        assertThrows(RuntimeException.class, () -> skillService.removeSkill(skillDTO, testUsername));
        verify(skillClient).removeSkill(any());
    }

    @Test
    void deleteAllSkills_ShouldCallClient_WhenUsernameIsValid() {
        assertDoesNotThrow(() -> skillService.deleteAllSkills(testUsername));

        verify(skillClient).deleteAllSkills(testUsername);
    }

    @Test
    void deleteAllSkills_ShouldThrowException_WhenClientFails() {
        doThrow(new RuntimeException("Client error")).when(skillClient).deleteAllSkills(testUsername);
 
        assertThrows(RuntimeException.class, () -> skillService.deleteAllSkills(testUsername));
        verify(skillClient).deleteAllSkills(testUsername);
    }
}
