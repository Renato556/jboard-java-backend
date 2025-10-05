package br.com.jboard.orchestrator.clients;

import br.com.jboard.orchestrator.models.Job;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobClientTest {
    @Mock
    RestClient restClient;
    @InjectMocks
    JobClient jobClient;

    @BeforeEach
    void setUp() throws Exception {
        Field urlField = JobClient.class.getDeclaredField("url");
        urlField.setAccessible(true);
        urlField.set(jobClient, "http://localhost");
    }

    @Test
    void getJobs_success() {
        RestClient.RequestHeadersUriSpec spec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec headersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);
        when(restClient.get()).thenReturn(spec);
        try {
            when(spec.uri(any(java.net.URI.class))).thenReturn(headersSpec);
        } catch (Exception ignored) {}
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(Collections.singletonList(new Job()));
        List<Job> jobs = jobClient.getJobs();
        assertNotNull(jobs);
        assertEquals(1, jobs.size());
    }

    @Test
    void getJobs_null_returnsEmptyList() {
        RestClient.RequestHeadersUriSpec spec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec headersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);
        when(restClient.get()).thenReturn(spec);
        try {
            when(spec.uri(any(java.net.URI.class))).thenReturn(headersSpec);
        } catch (Exception ignored) {}
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(null);
        List<Job> jobs = jobClient.getJobs();
        assertNotNull(jobs);
        assertTrue(jobs.isEmpty());
    }

    @Test
    void getJobs_throwsURISyntaxException() throws Exception {
        JobClient client = new JobClient(restClient);
        Field urlField = JobClient.class.getDeclaredField("url");
        urlField.setAccessible(true);
        urlField.set(client, "::::");
        RuntimeException ex = assertThrows(RuntimeException.class, client::getJobs);
        assertInstanceOf(URISyntaxException.class, ex.getCause());
    }

    @Test
    void getJobs_throwsRestClientException() {
        RestClient.RequestHeadersUriSpec spec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec headersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn(spec);
        try {
            when(spec.uri(any(java.net.URI.class))).thenReturn(headersSpec);
        } catch (Exception ignored) {}
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class)))
                .thenThrow(new org.springframework.web.client.RestClientException("Service unavailable"));

        assertThrows(org.springframework.web.client.RestClientException.class, () -> jobClient.getJobs());
    }
}
