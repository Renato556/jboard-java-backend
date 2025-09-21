package br.com.jboard.orchestrator.clients;

import br.com.jboard.orchestrator.models.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

@Component
public class JobClient {
    private static final Logger logger = LoggerFactory.getLogger(JobClient.class);
    @Value("${spring.data.crud.url}")
    private String url;

    private final RestClient restClient;

    public JobClient(RestClient restClient){
        this.restClient = restClient;
    }

    public List<Job> getJobs() {
        try {
            List<Job> jobs = restClient.get()
                    .uri(new URI(url))
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            return jobs == null ? Collections.emptyList() : jobs;
        } catch (URISyntaxException ex) {
            logger.error("Erro ao criar URI para buscar jobs: {}", ex.getMessage());
            throw new RuntimeException("Erro ao criar URI para buscar jobs", ex);
        }
    }
}
