package br.com.jboard.orchestrator.services;

import br.com.jboard.orchestrator.clients.UserClient;
import br.com.jboard.orchestrator.models.User;
import br.com.jboard.orchestrator.models.dtos.ChangePasswordDTO;
import br.com.jboard.orchestrator.models.dtos.RegisterDTO;
import br.com.jboard.orchestrator.models.enums.RoleEnum;
import br.com.jboard.orchestrator.models.exceptions.ForbiddenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthenticationService {
    private final UserClient userClient;

    public AuthenticationService(UserClient userClient){
        this.userClient = userClient;
    }

    public void registerUser(RegisterDTO data)  {
        var encryptedPassword = new BCryptPasswordEncoder().encode(data.getPassword());
        User newUser = new User(data.getUsername(), encryptedPassword, RoleEnum.FREE);

        userClient.registerUser(newUser);
        log.info("New user: {} registered successfully", data.getUsername());
    }

    public void updatePassword(ChangePasswordDTO data, String username) {
        var oldUser = userClient.getUserByUsername(username);
        var passwordEncoder = new BCryptPasswordEncoder();

        if (!passwordEncoder.matches(data.getOldPassword(), oldUser.getPassword())) {
            throw new ForbiddenException("Old password does not match");
        }

        var newEncryptedPassword = passwordEncoder.encode(data.getNewPassword());
        User updatedUser = new User(username, newEncryptedPassword, oldUser.getRole());

        if (!passwordEncoder.matches(data.getNewPassword(), oldUser.getPassword())) {
            userClient.updateUser(updatedUser);
        }
        log.info("Senha do usuário: {} alterada com sucesso", username);
    }

    public void deleteAccount(String username) {
        userClient.deleteAccount(username);
        log.info("Conta do usuário: {} deletada com sucesso", username);
    }
}
