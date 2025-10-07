package br.com.jboard.orchestrator.clients;

import br.com.jboard.orchestrator.models.AnalysisResponseDTO;
import br.com.jboard.orchestrator.models.dtos.AnalysisRequestDTO;
import br.com.jboard.orchestrator.models.exceptions.InternalServerErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Slf4j
@Component
public class AnalysisClient {
    @Value("${spring.data.analysis.url}")
    private String url;

    private final RestClient restClient;

    public AnalysisClient(RestClient restClient){
        this.restClient = restClient;
    }

    @Cacheable(value = "analysis", key = "T(br.com.jboard.orchestrator.clients.AnalysisClient).generateCacheKey(#analysisRequestDTO.position, #analysisRequestDTO.skills)")
    public AnalysisResponseDTO analyseMatch(AnalysisRequestDTO analysisRequestDTO) {
        log.info("Cache miss - Fazendo analise para a vaga: {} com as skills: {}", analysisRequestDTO.getPosition(), analysisRequestDTO.getSkills());
        try {
            return restClient.post()
                    .uri(new URI(url + "/analyse"))
                    .body(analysisRequestDTO)
                    .retrieve()
                    .body(AnalysisResponseDTO.class);
        } catch (URISyntaxException ex) {
            log.error("Erro ao criar URI para realizar análise de match por IA: {}", ex.getMessage());
            throw new InternalServerErrorException("Erro interno - URI inválida", ex);
        } catch (RestClientException ex) {
            log.error("Erro ao realizar análise de match por IA: {}", ex.getMessage());
            throw ex;
        }
    }

    public static String generateCacheKey(String position, List<String> skills) {
        return position + "_" + skills.stream()
                .sorted()
                .reduce("", (acc, skill) -> acc + skill + ",");
    }
}
