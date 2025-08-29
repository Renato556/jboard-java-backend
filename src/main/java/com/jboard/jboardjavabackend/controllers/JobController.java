package com.jboard.jboardjavabackend.controllers;

import com.jboard.jboardjavabackend.models.dtos.JobResponseDTO;
import com.jboard.jboardjavabackend.services.JobService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jobs")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    private int subtractPageNumber(int pageNumber) {
        return pageNumber - 1;
    }

    @GetMapping
    public ResponseEntity<JobResponseDTO> findAll(@RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "25") int size) {
        return ResponseEntity.ok(jobService.getJobs(PageRequest.of(subtractPageNumber(page), size)));
    }
}
