package br.com.jboard.orchestrator.models.dtos;

import br.com.jboard.orchestrator.models.NoSpaces;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ChangePasswordDTO {
    @NotEmpty
    @NoSpaces(message = "Senha atual não pode conter espaços")
    private String oldPassword;
    @NotEmpty
    @NoSpaces(message = "Nova senha não pode conter espaços")
    private String newPassword;
}
