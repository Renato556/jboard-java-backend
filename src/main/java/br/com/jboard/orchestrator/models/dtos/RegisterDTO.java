package br.com.jboard.orchestrator.models.dtos;

import br.com.jboard.orchestrator.models.NoSpaces;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RegisterDTO {
    @NotEmpty
    @NoSpaces(message = "Nome de usuário não pode conter espaços")
    private String username;
    @NotEmpty
    @NoSpaces(message = "Senha não pode conter espaços")
    private String password;
}
