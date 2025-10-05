package br.com.jboard.orchestrator.clients;

import br.com.jboard.orchestrator.models.Skill;
import br.com.jboard.orchestrator.models.dtos.SkillBackendResponseDTO;
import br.com.jboard.orchestrator.models.exceptions.InternalServerErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Slf4j
@Component
public class SkillClient {
    @Value("${spring.data.crud.url}")
    private String url;

    private final RestClient restClient;

    public SkillClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<Skill> getAllSkills(String username) {
        try {
            SkillBackendResponseDTO response = restClient.get()
                    .uri(new URI(url + "/skills?username=" + username))
                    .retrieve()
                    .body(SkillBackendResponseDTO.class);

            if (response == null || response.getSkills() == null) {
                return List.of();
            }

            return response.getSkills().stream()
                    .map(skill -> new Skill(response.getUsername(), skill))
                    .toList();
        } catch (URISyntaxException ex) {
            log.error("Erro ao criar URI para buscar skills: {}", ex.getMessage());
            throw new InternalServerErrorException("Erro interno - URI inválida", ex);
        } catch (RestClientException ex) {
            log.error("Erro ao buscar skills: {}", ex.getMessage());
            throw ex;
        }
    }

    public void addSkill(Skill skill) {
        try {
            restClient.post()
                    .uri(new URI(url + "/skills"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(skill)
                    .retrieve()
                    .toBodilessEntity();
        } catch (URISyntaxException ex) {
            log.error("Erro ao criar URI para adicionar skill: {}", ex.getMessage());
            throw new InternalServerErrorException("Erro interno - URI inválida", ex);
        } catch (RestClientException ex) {
            log.error("Erro ao adicionar skill: {}", ex.getMessage());
            throw ex;
        }
    }

    public void removeSkill(Skill skill) {
        try {
            restClient.put()
                    .uri(new URI(url + "/skills"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(skill)
                    .retrieve()
                    .toBodilessEntity();
        } catch (URISyntaxException ex) {
            log.error("Erro ao criar URI para remover skill: {}", ex.getMessage());
            throw new InternalServerErrorException("Erro interno - URI inválida", ex);
        } catch (RestClientException ex) {
            log.error("Erro ao remover skill: {}", ex.getMessage());
            throw ex;
        }
    }

    public void deleteAllSkills(String username) {
        try {
            restClient.delete()
                    .uri(new URI(url + "/skills?username=" + username))
                    .retrieve()
                    .toBodilessEntity();
        } catch (URISyntaxException ex) {
            log.error("Erro ao criar URI para deletar todas as skills: {}", ex.getMessage());
            throw new InternalServerErrorException("Erro interno - URI inválida", ex);
        } catch (RestClientException ex) {
            log.error("Erro ao deletar todas as skills: {}", ex.getMessage());
            throw ex;
        }
    }
}
