package br.com.jboard.orchestrator.controllers;

import br.com.jboard.orchestrator.models.AnalysisResponseDTO;
import br.com.jboard.orchestrator.models.dtos.AnalysisDTO;
import br.com.jboard.orchestrator.services.AnalysisService;
import br.com.jboard.orchestrator.utils.AuthUtils;
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
class AnalysisControllerTest {
    @Mock
    AnalysisService analysisService;
    @Mock
    AuthUtils authUtils;
    @Mock
    HttpServletRequest request;
    @InjectMocks
    AnalysisController analysisController;

    @Test
    void analyze_successfulAnalysis_returnsAnalysisResponse() {
        AnalysisDTO analysisDto = new AnalysisDTO();
        analysisDto.setPosition("Software Developer");

        String username = "testuser";
        AnalysisResponseDTO expectedResponse = new AnalysisResponseDTO();
        expectedResponse.setMessage("Analysis completed successfully");

        when(authUtils.getUsernameFromRequest(request)).thenReturn(username);
        when(analysisService.analyseMatch(analysisDto, username)).thenReturn(expectedResponse);

        ResponseEntity<AnalysisResponseDTO> response = analysisController.analyze(analysisDto, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedResponse.getMessage(), response.getBody().getMessage());
        verify(authUtils).getUsernameFromRequest(request);
        verify(analysisService).analyseMatch(analysisDto, username);
    }

    @Test
    void analyze_authUtilsThrowsException_propagatesException() {
        AnalysisDTO analysisDto = new AnalysisDTO();
        analysisDto.setPosition("Software Developer");

        when(authUtils.getUsernameFromRequest(request)).thenThrow(new RuntimeException("Invalid token"));

        assertThrows(RuntimeException.class, () -> analysisController.analyze(analysisDto, request));

        verify(authUtils).getUsernameFromRequest(request);
        verify(analysisService, never()).analyseMatch(any(), any());
    }

    @Test
    void analyze_analysisServiceThrowsException_propagatesException() {
        AnalysisDTO analysisDto = new AnalysisDTO();
        analysisDto.setPosition("Software Developer");

        String username = "testuser";

        when(authUtils.getUsernameFromRequest(request)).thenReturn(username);
        when(analysisService.analyseMatch(analysisDto, username))
                .thenThrow(new RuntimeException("Analysis service error"));

        assertThrows(RuntimeException.class, () -> analysisController.analyze(analysisDto, request));

        verify(authUtils).getUsernameFromRequest(request);
        verify(analysisService).analyseMatch(analysisDto, username);
    }

    @Test
    void analyze_nullAnalysisDto_handledByValidation() {
        String username = "testuser";

        when(authUtils.getUsernameFromRequest(request)).thenReturn(username);

        ResponseEntity<AnalysisResponseDTO> response = analysisController.analyze(null, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authUtils).getUsernameFromRequest(request);
        verify(analysisService).analyseMatch(null, username);
    }

    @Test
    void analyze_emptyPositionInDto_passedToService() {
        AnalysisDTO analysisDto = new AnalysisDTO();
        analysisDto.setPosition("");

        String username = "testuser";
        AnalysisResponseDTO expectedResponse = new AnalysisResponseDTO();
        expectedResponse.setMessage("Empty position analysis");

        when(authUtils.getUsernameFromRequest(request)).thenReturn(username);
        when(analysisService.analyseMatch(analysisDto, username)).thenReturn(expectedResponse);

        ResponseEntity<AnalysisResponseDTO> response = analysisController.analyze(analysisDto, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedResponse.getMessage(), response.getBody().getMessage());
        verify(authUtils).getUsernameFromRequest(request);
        verify(analysisService).analyseMatch(analysisDto, username);
    }

    @Test
    void analyze_nullUsernameFromAuth_passedToService() {
        AnalysisDTO analysisDto = new AnalysisDTO();
        analysisDto.setPosition("Software Developer");

        AnalysisResponseDTO expectedResponse = new AnalysisResponseDTO();
        expectedResponse.setMessage("Analysis with null username");

        when(authUtils.getUsernameFromRequest(request)).thenReturn(null);
        when(analysisService.analyseMatch(analysisDto, null)).thenReturn(expectedResponse);

        ResponseEntity<AnalysisResponseDTO> response = analysisController.analyze(analysisDto, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedResponse.getMessage(), response.getBody().getMessage());
        verify(authUtils).getUsernameFromRequest(request);
        verify(analysisService).analyseMatch(analysisDto, null);
    }
}
