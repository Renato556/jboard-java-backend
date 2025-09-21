package br.com.jboard.orchestrator.services;

import br.com.jboard.orchestrator.clients.JobClient;
import br.com.jboard.orchestrator.models.Job;
import br.com.jboard.orchestrator.models.dtos.JobResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {
    @Mock
    JobClient jobClient;
    @InjectMocks
    JobService jobService;

    @Test
    void getJobs_success() {
        List<Job> jobs = Collections.emptyList();
        when(jobClient.getJobs()).thenReturn(jobs);
        JobResponseDTO response = jobService.getJobs();
        assertNotNull(response);
        assertEquals(0, response.getMeta().getTotalRecords());
    }

    @Test
    void getJobs_exception() {
        when(jobClient.getJobs()).thenThrow(new RuntimeException("erro"));
        RuntimeException ex = assertThrows(RuntimeException.class, () -> jobService.getJobs());
        assertEquals("erro", ex.getMessage());
    }
}

