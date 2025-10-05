package br.com.jboard.orchestrator.clients;

import br.com.jboard.orchestrator.models.Job;
import br.com.jboard.orchestrator.models.exceptions.InternalServerErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class JobClient {
    @Value("${spring.data.crud.url}")
    private String url;

    private final RestClient restClient;

    public JobClient(RestClient restClient){
        this.restClient = restClient;
    }

    public List<Job> getJobs() {
        try {
            List<Job> jobs = restClient.get()
                    .uri(new URI(url + "/jobs"))
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            return jobs == null ? Collections.emptyList() : jobs;
        } catch (URISyntaxException ex) {
            log.error("Erro ao criar URI para buscar jobs: {}", ex.getMessage());
            throw new InternalServerErrorException("Erro interno - URI inválida", ex);
        } catch (RestClientException ex) {
            log.error("Erro ao buscar jobs: {}", ex.getMessage());
            throw ex;
        }
    }
}
