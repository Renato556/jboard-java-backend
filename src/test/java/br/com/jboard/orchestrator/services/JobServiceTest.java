package br.com.jboard.orchestrator.services;

import br.com.jboard.orchestrator.clients.JobClient;
import br.com.jboard.orchestrator.models.Job;
import br.com.jboard.orchestrator.models.dtos.JobResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

    @Test
    void getJobs_withMultipleJobs_returnsCorrectCount() {
        Job job1 = mock(Job.class);
        Job job2 = mock(Job.class);
        Job job3 = mock(Job.class);
        List<Job> jobs = Arrays.asList(job1, job2, job3);

        when(jobClient.getJobs()).thenReturn(jobs);

        JobResponseDTO response = jobService.getJobs();

        assertNotNull(response);
        assertEquals(3, response.getMeta().getTotalRecords());
        assertEquals(3, response.getData().size());
        verify(jobClient).getJobs();
    }

    @Test
    void getJobs_withSingleJob_returnsCorrectStructure() {
        Job job = mock(Job.class);
        List<Job> jobs = Collections.singletonList(job);

        when(jobClient.getJobs()).thenReturn(jobs);

        JobResponseDTO response = jobService.getJobs();

        assertNotNull(response);
        assertNotNull(response.getData());
        assertNotNull(response.getMeta());
        assertEquals(1, response.getMeta().getTotalRecords());
        assertEquals(1, response.getData().size());
    }

    @Test
    void getJobs_clientThrowsSpecificException_propagatesException() {
        RuntimeException specificException = new RuntimeException("Connection timeout");
        when(jobClient.getJobs()).thenThrow(specificException);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> jobService.getJobs());

        assertEquals("Connection timeout", thrown.getMessage());
        verify(jobClient).getJobs();
    }

    @Test
    void getJobs_nullReturnFromClient_handlesGracefully() {
        when(jobClient.getJobs()).thenReturn(null);

        assertThrows(NullPointerException.class, () -> jobService.getJobs());
        verify(jobClient).getJobs();
    }
}
