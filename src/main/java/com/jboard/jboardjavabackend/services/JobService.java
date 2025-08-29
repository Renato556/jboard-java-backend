package com.jboard.jboardjavabackend.services;

import com.jboard.jboardjavabackend.models.Job;
import com.jboard.jboardjavabackend.models.dtos.JobDTO;
import com.jboard.jboardjavabackend.models.dtos.JobResponseDTO;
import com.jboard.jboardjavabackend.models.dtos.MetaDTO;
import com.jboard.jboardjavabackend.repositories.JobRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JobService {
    JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    private JobResponseDTO buildJobResponseDTO(Page<Job> jobsPage) {
        return new JobResponseDTO(jobsPage.getContent().stream().map(JobDTO::new).toList(), buildMeta(jobsPage));
    }

    private MetaDTO buildMeta(Page<Job> jobsPage) {
        return new MetaDTO(jobsPage.getTotalElements(), jobsPage.getTotalPages());
    }

    @Cacheable(value = "jobsList", key = "'p='+#pageable.pageNumber+'|s='+#pageable.pageSize")
    public JobResponseDTO getJobs(Pageable pageable) {
        log.info("[getJobs] Getting all jobs with page-size {} for page {}", pageable.getPageSize(), pageable.getPageNumber());
        Page<Job> jobsPage = this.jobRepository.findAll(pageable);
        log.info("[getJobs] Found {} jobs in total, returning {} in page {}", jobsPage.getTotalElements(), jobsPage.getContent().size(), pageable.getPageNumber());

        return buildJobResponseDTO(jobsPage);
    }
}
