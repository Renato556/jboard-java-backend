package br.com.jboard.orchestrator.services;

import br.com.jboard.orchestrator.clients.UserClient;
import br.com.jboard.orchestrator.models.dtos.ChangeRoleDTO;
import br.com.jboard.orchestrator.models.exceptions.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Slf4j
@Service
public class RoleService {
    private final UserClient userClient;

    @Value("${app.admin.credentials}")
    private String adminCredentials;

    public RoleService(UserClient userClient) {
        this.userClient = userClient;
    }

    public void updateRole(ChangeRoleDTO data, String authorizationHeader) {
        validateAdminCredentials(authorizationHeader);
        var user = userClient.getUserByUsername(data.getUsername());

        if (user.getRole() == data.getRole()) {
            log.info("A role do usuário {} já é {}", user.getUsername(), data.getRole());
            return;
        }

        user.setRole(data.getRole());
        userClient.updateUser(user);
        log.info("Role do usuário {} alterada para {}", user.getUsername(), data.getRole());
    }

    private void validateAdminCredentials(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Basic ")) {
            throw new UnauthorizedException("Basic authentication required");
        }

        String base64Credentials = authorizationHeader.substring("Basic ".length());
        String providedCredentials = new String(Base64.getDecoder().decode(base64Credentials));
        String expectedCredentials = new String(Base64.getDecoder().decode(adminCredentials));

        if (!providedCredentials.equals(expectedCredentials)) {
            throw new UnauthorizedException("Invalid admin credentials");
        }
    }
}
