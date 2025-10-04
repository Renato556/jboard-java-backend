package br.com.jboard.orchestrator.clients;

import br.com.jboard.orchestrator.models.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.net.URISyntaxException;

import static java.lang.String.format;

@Slf4j
@Component
public class UserClient {
    @Value("${spring.data.crud.url}")
    private String url;

    private final RestClient restClient;

    public UserClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public User getUserById(String id) {
        return getUser( "id", id);
    }

    public User getUserByUsername(String username) {
        return getUser( "username", username);
    }

    public void registerUser(User newUser) {
        try {
            restClient.post()
                    .uri(new URI(url + "/users"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(newUser)
                    .retrieve()
                    .toBodilessEntity();
        } catch (URISyntaxException ex) {
            log.error("Erro ao criar URI para registrar usuário: {}", ex.getMessage());
            throw new RuntimeException("Erro ao criar URI para registrar usuário", ex);
        }
    }

    public void updateUser(User user) {
        try {
            restClient.put()
                    .uri(new URI(url + "/users"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(user)
                    .retrieve()
                    .toBodilessEntity();
        } catch (URISyntaxException ex) {
            log.error("Erro ao criar URI para atualizar senha do usuário: {}", ex.getMessage());
            throw new RuntimeException("Erro ao criar URI para atualizar senha do usuário", ex);
        }
    }

    public void deleteAccount(String username) {
        try {
            restClient.delete()
                    .uri(new URI(url + "/users?username=" + username))
                    .retrieve()
                    .toBodilessEntity();
        } catch (URISyntaxException ex) {
            log.error("Erro ao criar URI para deletar usuário: {}", ex.getMessage());
            throw new RuntimeException("Erro ao criar URI para deletar usuário", ex);
        }
    }

    private User getUser(String param, String value) {
        try {
            return restClient.get()
                    .uri(new URI(format("%s/users?%s=%s", url, param, value)))
                    .retrieve()
                    .body(User.class);
        } catch (URISyntaxException ex) {
            log.error("Erro ao criar URI para buscar usuário por:{}. Exception: {}", param, ex.getMessage());
            throw new RuntimeException("Erro ao criar URI para buscar usuário", ex);
        }
    }
}
