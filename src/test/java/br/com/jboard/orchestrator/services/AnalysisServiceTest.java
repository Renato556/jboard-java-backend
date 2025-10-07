package br.com.jboard.orchestrator.services;

import br.com.jboard.orchestrator.clients.AnalysisClient;
import br.com.jboard.orchestrator.models.AnalysisResponseDTO;
import br.com.jboard.orchestrator.models.dtos.AnalysisDTO;
import br.com.jboard.orchestrator.models.dtos.AnalysisRequestDTO;
import br.com.jboard.orchestrator.models.dtos.MetaDTO;
import br.com.jboard.orchestrator.models.dtos.SkillResponseDTO;
import br.com.jboard.orchestrator.models.exceptions.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalysisServiceTest {

    @Mock
    private AnalysisClient analysisClient;

    @Mock
    private SkillService skillService;

    @InjectMocks
    private AnalysisService analysisService;

    private final String testUsername = "testuser";

    @Test
    void analyseMatch_ShouldReturnAnalysisResponse_WhenUserHasSkills() {
        AnalysisDTO analysisDto = new AnalysisDTO();
        analysisDto.setPosition("Software Developer");

        List<String> skills = List.of("java", "python", "spring");
        SkillResponseDTO skillResponse = new SkillResponseDTO(skills, new MetaDTO(3));
        AnalysisResponseDTO expectedResponse = new AnalysisResponseDTO();
        expectedResponse.setMessage("Match analysis completed");

        when(skillService.getAllSkills(testUsername)).thenReturn(skillResponse);
        when(analysisClient.analyseMatch(any(AnalysisRequestDTO.class))).thenReturn(expectedResponse);

        AnalysisResponseDTO result = analysisService.analyseMatch(analysisDto, testUsername);

        assertNotNull(result);
        assertEquals(expectedResponse.getMessage(), result.getMessage());
        verify(skillService).getAllSkills(testUsername);
        verify(analysisClient).analyseMatch(any(AnalysisRequestDTO.class));
    }

    @Test
    void analyseMatch_ShouldThrowBadRequestException_WhenUserHasNoSkills() {
        AnalysisDTO analysisDto = new AnalysisDTO();
        analysisDto.setPosition("Software Developer");

        SkillResponseDTO emptySkillResponse = new SkillResponseDTO(List.of(), new MetaDTO(0));

        when(skillService.getAllSkills(testUsername)).thenReturn(emptySkillResponse);

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> analysisService.analyseMatch(analysisDto, testUsername));

        assertEquals("No skills found for user " + testUsername, exception.getMessage());
        verify(skillService).getAllSkills(testUsername);
        verify(analysisClient, never()).analyseMatch(any());
    }

    @Test
    void analyseMatch_ShouldPassCorrectRequestToClient_WhenCalledWithValidData() {
        AnalysisDTO analysisDto = new AnalysisDTO();
        analysisDto.setPosition("Java Developer");

        List<String> skills = List.of("java", "spring", "hibernate");
        SkillResponseDTO skillResponse = new SkillResponseDTO(skills, new MetaDTO(3));
        AnalysisResponseDTO expectedResponse = new AnalysisResponseDTO();
        expectedResponse.setMessage("Analysis completed");

        when(skillService.getAllSkills(testUsername)).thenReturn(skillResponse);
        when(analysisClient.analyseMatch(any(AnalysisRequestDTO.class))).thenReturn(expectedResponse);

        analysisService.analyseMatch(analysisDto, testUsername);

        verify(analysisClient).analyseMatch(argThat(request ->
                request.getPosition().equals("Java Developer") &&
                request.getSkills().equals(skills)
        ));
    }

    @Test
    void analyseMatch_ShouldThrowException_WhenSkillServiceFails() {
        AnalysisDTO analysisDto = new AnalysisDTO();
        analysisDto.setPosition("Software Developer");

        when(skillService.getAllSkills(testUsername)).thenThrow(new RuntimeException("Skill service error"));

        assertThrows(RuntimeException.class, () -> analysisService.analyseMatch(analysisDto, testUsername));

        verify(skillService).getAllSkills(testUsername);
        verify(analysisClient, never()).analyseMatch(any());
    }

    @Test
    void analyseMatch_ShouldThrowException_WhenAnalysisClientFails() {
        AnalysisDTO analysisDto = new AnalysisDTO();
        analysisDto.setPosition("Software Developer");

        List<String> skills = List.of("java");
        SkillResponseDTO skillResponse = new SkillResponseDTO(skills, new MetaDTO(1));

        when(skillService.getAllSkills(testUsername)).thenReturn(skillResponse);
        when(analysisClient.analyseMatch(any(AnalysisRequestDTO.class)))
                .thenThrow(new RuntimeException("Analysis client error"));

        assertThrows(RuntimeException.class, () -> analysisService.analyseMatch(analysisDto, testUsername));

        verify(skillService).getAllSkills(testUsername);
        verify(analysisClient).analyseMatch(any());
    }

    @Test
    void analyseMatch_ShouldHandleNullPosition_WhenPassedToClient() {
        AnalysisDTO analysisDto = new AnalysisDTO();
        analysisDto.setPosition(null);

        List<String> skills = List.of("java");
        SkillResponseDTO skillResponse = new SkillResponseDTO(skills, new MetaDTO(1));
        AnalysisResponseDTO expectedResponse = new AnalysisResponseDTO();

        when(skillService.getAllSkills(testUsername)).thenReturn(skillResponse);
        when(analysisClient.analyseMatch(any(AnalysisRequestDTO.class))).thenReturn(expectedResponse);

        AnalysisResponseDTO result = analysisService.analyseMatch(analysisDto, testUsername);

        assertNotNull(result);
        verify(analysisClient).analyseMatch(argThat(request ->
                request.getPosition() == null &&
                request.getSkills().equals(skills)
        ));
    }

    @Test
    void analyseMatch_ShouldHandleEmptyPosition_WhenPassedToClient() {
        AnalysisDTO analysisDto = new AnalysisDTO();
        analysisDto.setPosition("");

        List<String> skills = List.of("python", "django");
        SkillResponseDTO skillResponse = new SkillResponseDTO(skills, new MetaDTO(2));
        AnalysisResponseDTO expectedResponse = new AnalysisResponseDTO();

        when(skillService.getAllSkills(testUsername)).thenReturn(skillResponse);
        when(analysisClient.analyseMatch(any(AnalysisRequestDTO.class))).thenReturn(expectedResponse);

        AnalysisResponseDTO result = analysisService.analyseMatch(analysisDto, testUsername);

        assertNotNull(result);
        verify(analysisClient).analyseMatch(argThat(request ->
                request.getPosition().isEmpty() &&
                request.getSkills().equals(skills)
        ));
    }

    @Test
    void analyseMatch_ShouldHandleNullUsername_WhenPassedToSkillService() {
        AnalysisDTO analysisDto = new AnalysisDTO();
        analysisDto.setPosition("Developer");

        List<String> skills = List.of("javascript");
        SkillResponseDTO skillResponse = new SkillResponseDTO(skills, new MetaDTO(1));
        AnalysisResponseDTO expectedResponse = new AnalysisResponseDTO();

        when(skillService.getAllSkills(null)).thenReturn(skillResponse);
        when(analysisClient.analyseMatch(any(AnalysisRequestDTO.class))).thenReturn(expectedResponse);

        AnalysisResponseDTO result = analysisService.analyseMatch(analysisDto, null);

        assertNotNull(result);
        verify(skillService).getAllSkills(null);
        verify(analysisClient).analyseMatch(any());
    }
}
