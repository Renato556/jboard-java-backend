package br.com.jboard.orchestrator.clients;

import br.com.jboard.orchestrator.models.AnalysisResponseDTO;
import br.com.jboard.orchestrator.models.dtos.AnalysisRequestDTO;
import br.com.jboard.orchestrator.models.exceptions.InternalServerErrorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalysisClientTest {
    @Mock
    RestClient restClient;
    @InjectMocks
    AnalysisClient analysisClient;

    @BeforeEach
    void setUp() throws Exception {
        Field urlField = AnalysisClient.class.getDeclaredField("url");
        urlField.setAccessible(true);
        urlField.set(analysisClient, "http://localhost");
    }

    @Test
    void analyseMatch_withValidRequest_returnsAnalysisResponse() {
        AnalysisRequestDTO request = new AnalysisRequestDTO("Software Developer", List.of("java", "spring"));
        AnalysisResponseDTO expectedResponse = new AnalysisResponseDTO();
        expectedResponse.setMessage("Analysis completed successfully");

        RestClient.RequestBodyUriSpec spec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.RequestBodySpec bodySpec = mock(RestClient.RequestBodySpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.post()).thenReturn(spec);
        try {
            when(spec.uri(any(java.net.URI.class))).thenReturn(bodySpec);
        } catch (Exception ignored) {}
        when(bodySpec.body(request)).thenReturn(bodySpec);
        when(bodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(AnalysisResponseDTO.class)).thenReturn(expectedResponse);

        AnalysisResponseDTO result = analysisClient.analyseMatch(request);

        assertNotNull(result);
        assertEquals(expectedResponse.getMessage(), result.getMessage());
    }

    @Test
    void analyseMatch_withNullResponse_returnsNull() {
        AnalysisRequestDTO request = new AnalysisRequestDTO("Developer", List.of("python"));

        RestClient.RequestBodyUriSpec spec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.RequestBodySpec bodySpec = mock(RestClient.RequestBodySpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.post()).thenReturn(spec);
        try {
            when(spec.uri(any(java.net.URI.class))).thenReturn(bodySpec);
        } catch (Exception ignored) {}
        when(bodySpec.body(request)).thenReturn(bodySpec);
        when(bodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(AnalysisResponseDTO.class)).thenReturn(null);

        AnalysisResponseDTO result = analysisClient.analyseMatch(request);

        assertNull(result);
    }

    @Test
    void analyseMatch_withEmptySkillsList_returnsAnalysisResponse() {
        AnalysisRequestDTO request = new AnalysisRequestDTO("Manager", List.of());
        AnalysisResponseDTO expectedResponse = new AnalysisResponseDTO();
        expectedResponse.setMessage("No skills to analyze");

        RestClient.RequestBodyUriSpec spec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.RequestBodySpec bodySpec = mock(RestClient.RequestBodySpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.post()).thenReturn(spec);
        try {
            when(spec.uri(any(java.net.URI.class))).thenReturn(bodySpec);
        } catch (Exception ignored) {}
        when(bodySpec.body(request)).thenReturn(bodySpec);
        when(bodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(AnalysisResponseDTO.class)).thenReturn(expectedResponse);

        AnalysisResponseDTO result = analysisClient.analyseMatch(request);

        assertNotNull(result);
        assertEquals(expectedResponse.getMessage(), result.getMessage());
    }

    @Test
    void analyseMatch_withNullPosition_returnsAnalysisResponse() {
        AnalysisRequestDTO request = new AnalysisRequestDTO(null, List.of("javascript"));
        AnalysisResponseDTO expectedResponse = new AnalysisResponseDTO();
        expectedResponse.setMessage("Position analysis with null");

        RestClient.RequestBodyUriSpec spec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.RequestBodySpec bodySpec = mock(RestClient.RequestBodySpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.post()).thenReturn(spec);
        try {
            when(spec.uri(any(java.net.URI.class))).thenReturn(bodySpec);
        } catch (Exception ignored) {}
        when(bodySpec.body(request)).thenReturn(bodySpec);
        when(bodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(AnalysisResponseDTO.class)).thenReturn(expectedResponse);

        AnalysisResponseDTO result = analysisClient.analyseMatch(request);

        assertNotNull(result);
        assertEquals(expectedResponse.getMessage(), result.getMessage());
    }

    @Test
    void analyseMatch_withInvalidUrl_throwsInternalServerErrorException() throws Exception {
        AnalysisClient client = new AnalysisClient(restClient);
        Field urlField = AnalysisClient.class.getDeclaredField("url");
        urlField.setAccessible(true);
        urlField.set(client, "::::");

        AnalysisRequestDTO request = new AnalysisRequestDTO("Developer", List.of("java"));

        InternalServerErrorException ex = assertThrows(InternalServerErrorException.class,
            () -> client.analyseMatch(request));

        assertEquals("Erro interno - URI inválida", ex.getMessage());
        assertNotNull(ex.getCause());
    }

    @Test
    void analyseMatch_withRestClientException_throwsRestClientException() {
        AnalysisRequestDTO request = new AnalysisRequestDTO("Developer", List.of("java"));

        RestClient.RequestBodyUriSpec spec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.RequestBodySpec bodySpec = mock(RestClient.RequestBodySpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.post()).thenReturn(spec);
        try {
            when(spec.uri(any(java.net.URI.class))).thenReturn(bodySpec);
        } catch (Exception ignored) {}
        when(bodySpec.body(request)).thenReturn(bodySpec);
        when(bodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(AnalysisResponseDTO.class))
            .thenThrow(new RestClientException("Service unavailable"));

        assertThrows(RestClientException.class, () -> analysisClient.analyseMatch(request));
    }
}
