package br.com.jboard.orchestrator.clients;

import br.com.jboard.orchestrator.models.Skill;
import br.com.jboard.orchestrator.models.dtos.SkillBackendResponseDTO;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillClientTest {
    @Mock
    RestClient restClient;
    @InjectMocks
    SkillClient skillClient;

    @BeforeEach
    void setUp() throws Exception {
        Field urlField = SkillClient.class.getDeclaredField("url");
        urlField.setAccessible(true);
        urlField.set(skillClient, "http://localhost");
    }

    @Test
    void getAllSkills_withValidResponse_returnsSkillList() {
        String username = "testuser";
        SkillBackendResponseDTO response = new SkillBackendResponseDTO(username, Arrays.asList("Java", "Python"));

        RestClient.RequestHeadersUriSpec spec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec headersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn(spec);
        try {
            when(spec.uri(any(java.net.URI.class))).thenReturn(headersSpec);
        } catch (Exception ignored) {}
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(SkillBackendResponseDTO.class)).thenReturn(response);

        List<Skill> skills = skillClient.getAllSkills(username);

        assertNotNull(skills);
        assertEquals(2, skills.size());
        assertEquals("testuser", skills.get(0).getUsername());
        assertEquals("Java", skills.get(0).getSkill());
        assertEquals("testuser", skills.get(1).getUsername());
        assertEquals("Python", skills.get(1).getSkill());
    }

    @Test
    void getAllSkills_withNullResponse_returnsEmptyList() {
        String username = "testuser";

        RestClient.RequestHeadersUriSpec spec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec headersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn(spec);
        try {
            when(spec.uri(any(java.net.URI.class))).thenReturn(headersSpec);
        } catch (Exception ignored) {}
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(SkillBackendResponseDTO.class)).thenReturn(null);

        List<Skill> skills = skillClient.getAllSkills(username);

        assertNotNull(skills);
        assertTrue(skills.isEmpty());
    }

    @Test
    void getAllSkills_withNullSkillsList_returnsEmptyList() {
        String username = "testuser";
        SkillBackendResponseDTO response = new SkillBackendResponseDTO(username, null);

        RestClient.RequestHeadersUriSpec spec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec headersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn(spec);
        try {
            when(spec.uri(any(java.net.URI.class))).thenReturn(headersSpec);
        } catch (Exception ignored) {}
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(SkillBackendResponseDTO.class)).thenReturn(response);

        List<Skill> skills = skillClient.getAllSkills(username);

        assertNotNull(skills);
        assertTrue(skills.isEmpty());
    }

    @Test
    void getAllSkills_withEmptySkillsList_returnsEmptyList() {
        String username = "testuser";
        SkillBackendResponseDTO response = new SkillBackendResponseDTO(username, Collections.emptyList());

        RestClient.RequestHeadersUriSpec spec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec headersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn(spec);
        try {
            when(spec.uri(any(java.net.URI.class))).thenReturn(headersSpec);
        } catch (Exception ignored) {}
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(SkillBackendResponseDTO.class)).thenReturn(response);

        List<Skill> skills = skillClient.getAllSkills(username);

        assertNotNull(skills);
        assertTrue(skills.isEmpty());
    }

    @Test
    void getAllSkills_withInvalidUrl_throwsInternalServerErrorException() throws Exception {
        SkillClient client = new SkillClient(restClient);
        Field urlField = SkillClient.class.getDeclaredField("url");
        urlField.setAccessible(true);
        urlField.set(client, "::::");

        InternalServerErrorException ex = assertThrows(InternalServerErrorException.class,
            () -> client.getAllSkills("testuser"));

        assertEquals("Erro interno - URI inválida", ex.getMessage());
        assertNotNull(ex.getCause());
    }

    @Test
    void getAllSkills_withRestClientException_throwsRestClientException() {
        String username = "testuser";

        RestClient.RequestHeadersUriSpec spec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec headersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn(spec);
        try {
            when(spec.uri(any(java.net.URI.class))).thenReturn(headersSpec);
        } catch (Exception ignored) {}
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(SkillBackendResponseDTO.class))
            .thenThrow(new RestClientException("Service unavailable"));

        assertThrows(RestClientException.class, () -> skillClient.getAllSkills(username));
    }

    @Test
    void addSkill_withValidSkill_completesSuccessfully() {
        Skill skill = new Skill("testuser", "Java");

        RestClient.RequestBodyUriSpec spec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.RequestBodySpec bodySpec = mock(RestClient.RequestBodySpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.post()).thenReturn(spec);
        try {
            when(spec.uri(any(java.net.URI.class))).thenReturn(bodySpec);
        } catch (Exception ignored) {}
        when(bodySpec.contentType(any())).thenReturn(bodySpec);
        when(bodySpec.body(skill)).thenReturn(bodySpec);
        when(bodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(null);

        assertDoesNotThrow(() -> skillClient.addSkill(skill));
    }

    @Test
    void addSkill_withInvalidUrl_throwsInternalServerErrorException() throws Exception {
        SkillClient client = new SkillClient(restClient);
        Field urlField = SkillClient.class.getDeclaredField("url");
        urlField.setAccessible(true);
        urlField.set(client, "::::");

        Skill skill = new Skill("testuser", "Java");

        InternalServerErrorException ex = assertThrows(InternalServerErrorException.class,
            () -> client.addSkill(skill));

        assertEquals("Erro interno - URI inválida", ex.getMessage());
        assertNotNull(ex.getCause());
    }

    @Test
    void addSkill_withRestClientException_throwsRestClientException() {
        Skill skill = new Skill("testuser", "Java");

        RestClient.RequestBodyUriSpec spec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.RequestBodySpec bodySpec = mock(RestClient.RequestBodySpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.post()).thenReturn(spec);
        try {
            when(spec.uri(any(java.net.URI.class))).thenReturn(bodySpec);
        } catch (Exception ignored) {}
        when(bodySpec.contentType(any())).thenReturn(bodySpec);
        when(bodySpec.body(skill)).thenReturn(bodySpec);
        when(bodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenThrow(new RestClientException("Service unavailable"));

        assertThrows(RestClientException.class, () -> skillClient.addSkill(skill));
    }

    @Test
    void removeSkill_withValidSkill_completesSuccessfully() {
        Skill skill = new Skill("testuser", "Java");

        RestClient.RequestBodyUriSpec spec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.RequestBodySpec bodySpec = mock(RestClient.RequestBodySpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.put()).thenReturn(spec);
        try {
            when(spec.uri(any(java.net.URI.class))).thenReturn(bodySpec);
        } catch (Exception ignored) {}
        when(bodySpec.contentType(any())).thenReturn(bodySpec);
        when(bodySpec.body(skill)).thenReturn(bodySpec);
        when(bodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(null);

        assertDoesNotThrow(() -> skillClient.removeSkill(skill));
    }

    @Test
    void removeSkill_withInvalidUrl_throwsInternalServerErrorException() throws Exception {
        SkillClient client = new SkillClient(restClient);
        Field urlField = SkillClient.class.getDeclaredField("url");
        urlField.setAccessible(true);
        urlField.set(client, "::::");

        Skill skill = new Skill("testuser", "Java");

        InternalServerErrorException ex = assertThrows(InternalServerErrorException.class,
            () -> client.removeSkill(skill));

        assertEquals("Erro interno - URI inválida", ex.getMessage());
        assertNotNull(ex.getCause());
    }

    @Test
    void removeSkill_withRestClientException_throwsRestClientException() {
        Skill skill = new Skill("testuser", "Java");

        RestClient.RequestBodyUriSpec spec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.RequestBodySpec bodySpec = mock(RestClient.RequestBodySpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.put()).thenReturn(spec);
        try {
            when(spec.uri(any(java.net.URI.class))).thenReturn(bodySpec);
        } catch (Exception ignored) {}
        when(bodySpec.contentType(any())).thenReturn(bodySpec);
        when(bodySpec.body(skill)).thenReturn(bodySpec);
        when(bodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenThrow(new RestClientException("Service unavailable"));

        assertThrows(RestClientException.class, () -> skillClient.removeSkill(skill));
    }

    @Test
    void deleteAllSkills_withValidUsername_completesSuccessfully() {
        String username = "testuser";

        RestClient.RequestHeadersUriSpec spec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec headersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.delete()).thenReturn(spec);
        try {
            when(spec.uri(any(java.net.URI.class))).thenReturn(headersSpec);
        } catch (Exception ignored) {}
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(null);

        assertDoesNotThrow(() -> skillClient.deleteAllSkills(username));
    }

    @Test
    void deleteAllSkills_withInvalidUrl_throwsInternalServerErrorException() throws Exception {
        SkillClient client = new SkillClient(restClient);
        Field urlField = SkillClient.class.getDeclaredField("url");
        urlField.setAccessible(true);
        urlField.set(client, "::::");

        InternalServerErrorException ex = assertThrows(InternalServerErrorException.class,
            () -> client.deleteAllSkills("testuser"));

        assertEquals("Erro interno - URI inválida", ex.getMessage());
        assertNotNull(ex.getCause());
    }

    @Test
    void deleteAllSkills_withRestClientException_throwsRestClientException() {
        String username = "testuser";

        RestClient.RequestHeadersUriSpec spec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec headersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.delete()).thenReturn(spec);
        try {
            when(spec.uri(any(java.net.URI.class))).thenReturn(headersSpec);
        } catch (Exception ignored) {}
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenThrow(new RestClientException("Service unavailable"));

        assertThrows(RestClientException.class, () -> skillClient.deleteAllSkills(username));
    }
}
