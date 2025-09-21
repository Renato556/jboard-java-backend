package br.com.jboard.orchestrator.services;

import br.com.jboard.orchestrator.clients.JobClient;
import br.com.jboard.orchestrator.models.Job;
import br.com.jboard.orchestrator.models.dtos.JobDTO;
import br.com.jboard.orchestrator.models.dtos.JobResponseDTO;
import br.com.jboard.orchestrator.models.dtos.MetaDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class JobService {
    JobClient jobClient;

    public JobService(JobClient jobClient) {
        this.jobClient = jobClient;
    }

    private JobResponseDTO buildJobResponseDTO(List<Job> jobList) {
        return new JobResponseDTO(jobList.stream().map(JobDTO::new).toList(), buildMeta(jobList));
    }

    private MetaDTO buildMeta(List<Job> jobList) {
        return new MetaDTO(jobList.size());
    }

    public JobResponseDTO getJobs() {
        log.info("[getJobs] Getting all jobs");
        try {
            List<Job> jobList = jobClient.getJobs();

            log.info("[getJobs] Found {} jobs", jobList.size());
            return buildJobResponseDTO(jobList);
        } catch (Exception ex) {
            log.error("[getJobs] Error: {}", ex.getMessage());
            throw ex;
        }
    }
}
