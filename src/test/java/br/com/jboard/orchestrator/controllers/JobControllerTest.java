package br.com.jboard.orchestrator.controllers;

import br.com.jboard.orchestrator.models.dtos.JobResponseDTO;
import br.com.jboard.orchestrator.services.JobService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobControllerTest {
    @Mock
    JobService jobService;
    @InjectMocks
    JobController jobController;

    @Test
    void findAll_success() {
        JobResponseDTO dto = mock(JobResponseDTO.class);
        when(jobService.getJobs()).thenReturn(dto);
        ResponseEntity<JobResponseDTO> response = jobController.findAll();
        assertEquals(200, response.getStatusCode().value());
        assertEquals(dto, response.getBody());
    }

    @Test
    void findAll_exception() {
        when(jobService.getJobs()).thenThrow(new RuntimeException("erro"));
        RuntimeException ex = assertThrows(RuntimeException.class, () -> jobController.findAll());
        assertEquals("erro", ex.getMessage());
    }
}