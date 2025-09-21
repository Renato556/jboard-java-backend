package br.com.jboard.orchestrator.controllers;

import br.com.jboard.orchestrator.models.dtos.JobResponseDTO;
import br.com.jboard.orchestrator.services.JobService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jobs")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping
    public ResponseEntity<JobResponseDTO> findAll() {
        return ResponseEntity.ok(jobService.getJobs());
    }
}
